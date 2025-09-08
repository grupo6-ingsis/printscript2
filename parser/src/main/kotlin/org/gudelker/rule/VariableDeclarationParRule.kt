package org.gudelker.rule

import org.gudelker.CanBeCallStatement
import org.gudelker.VariableDeclaration
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.tokenstream.TokenStream

class VariableDeclarationParRule(
    private val keywords: Set<String>,
    private val expressionRule: SyntaxParRule,
) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        val current = tokenStream.current()
        return current?.getType() == TokenType.KEYWORD && keywords.contains(current.getValue())
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        // Consume keyword
        val (keywordToken, streamAfterKeyword) = tokenStream.consume(TokenType.KEYWORD)

        if (keywordToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba una palabra clave al inicio de la declaración"), tokenStream)
        }
        val tokenPosition = keywordToken.getPosition()
        val position = StatementPosition(tokenPosition.startLine, tokenPosition.startColumn, tokenPosition.endLine, tokenPosition.endColumn)

        // Consume identifier
        val (identifierToken, streamAfterIdentifier) = streamAfterKeyword.consume(TokenType.IDENTIFIER)
        if (identifierToken == null) {
            return ParseResult(
                ParserSyntaxError("Se esperaba un identificador después de '${keywordToken.getValue()}'"),
                streamAfterKeyword,
            )
        }
        val identifierPosition = identifierToken.getPosition()
        val identifierPos =
            StatementPosition(
                identifierPosition.startLine,
                identifierPosition.startColumn,
                identifierPosition.endLine,
                identifierPosition.endColumn,
            )

        // Optional type declaration
        val (type, streamAfterType) = parseOptionalType(streamAfterIdentifier)
        if (type.second != null) {
            return ParseResult(type.second!!, streamAfterType)
        }

        // Assignation
        val (assignToken, streamAfterAssign) = streamAfterType.consume(TokenType.ASSIGNATION)
        if (assignToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '=' después de la declaración"), streamAfterType)
        }

        // Expression
        val expressionResult = expressionRule.parse(streamAfterAssign)
        if (expressionResult.parserResult !is ValidStatementParserResult) {
            return ParseResult(ParserSyntaxError("Error al parsear la expresión"), expressionResult.tokenStream)
        }

        val expressionStatement = expressionResult.parserResult.getStatement() as CanBeCallStatement

        // Semicolon
        val (semicolonToken, finalStream) = expressionResult.tokenStream.consume(TokenType.SEMICOLON)
        if (semicolonToken == null) {
            return ParseResult(
                ParserSyntaxError("Se esperaba un punto y coma al final de la declaración"),
                expressionResult.tokenStream,
            )
        }

        val statement =
            VariableDeclaration(
                ComboValuePosition(keywordToken.getValue(), position),
                ComboValuePosition(identifierToken.getValue(), identifierPos),
                type.first,
                expressionStatement,
            )
        return ParseResult(ValidStatementParserResult(statement), finalStream)
    }

    private fun parseOptionalType(stream: TokenStream): Pair<Pair<String?, ParserSyntaxError?>, TokenStream> {
        return if (stream.check(TokenType.COLON)) {
            val (_, streamAfterColon) = stream.consume(TokenType.COLON)
            val (typeToken, streamAfterType) = streamAfterColon.consume(TokenType.TYPE)
            if (typeToken == null) {
                (null to ParserSyntaxError("Se esperaba un tipo después de ':'")) to streamAfterColon
            } else {
                (typeToken.getValue() to null) to streamAfterType
            }
        } else {
            (null to null) to stream
        }
    }
}
