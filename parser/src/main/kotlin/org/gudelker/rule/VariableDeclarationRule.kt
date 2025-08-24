package org.gudelker.rule

import org.example.org.gudelker.ExpressionStatement
import org.example.org.gudelker.VariableDeclaration
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.SyntaxError
import org.gudelker.result.ValidStatementResult
import org.gudelker.tokenstream.TokenStream

class VariableDeclarationRule(
    private val keywords: Set<String>,
    private val expressionRule: SyntaxRule,
) : SyntaxRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        val current = tokenStream.current()
        return current?.getType() == TokenType.KEYWORD && keywords.contains(current.getValue())
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        // Consume keyword
        val (keywordToken, streamAfterKeyword) = tokenStream.consume(TokenType.KEYWORD)
        if (keywordToken == null) {
            return ParseResult(SyntaxError("Se esperaba una palabra clave al inicio de la declaración"), tokenStream)
        }

        // Consume identifier
        val (identifierToken, streamAfterIdentifier) = streamAfterKeyword.consume(TokenType.IDENTIFIER)
        if (identifierToken == null) {
            return ParseResult(
                SyntaxError("Se esperaba un identificador después de '${keywordToken.getValue()}'"),
                streamAfterKeyword,
            )
        }

        // Optional type declaration
        val (type, streamAfterType) = parseOptionalType(streamAfterIdentifier)
        if (type.second != null) {
            return ParseResult(type.second!!, streamAfterType)
        }

        // Assignation
        val (assignToken, streamAfterAssign) = streamAfterType.consume(TokenType.ASSIGNATION)
        if (assignToken == null) {
            return ParseResult(SyntaxError("Se esperaba '=' después de la declaración"), streamAfterType)
        }

        // Expression
        val expressionResult = expressionRule.parse(streamAfterAssign)
        if (expressionResult.result !is ValidStatementResult) {
            return ParseResult(SyntaxError("Error al parsear la expresión"), expressionResult.tokenStream)
        }

        val expressionStatement = expressionResult.result.getStatement() as ExpressionStatement

        // Semicolon
        val (semicolonToken, finalStream) = expressionResult.tokenStream.consume(TokenType.SEMICOLON)
        if (semicolonToken == null) {
            return ParseResult(
                SyntaxError("Se esperaba un punto y coma al final de la declaración"),
                expressionResult.tokenStream,
            )
        }

        val statement = VariableDeclaration(identifierToken.getValue(), type.first, expressionStatement)
        return ParseResult(ValidStatementResult(statement), finalStream)
    }

    private fun parseOptionalType(stream: TokenStream): Pair<Pair<String?, SyntaxError?>, TokenStream> {
        return if (stream.check(TokenType.COLON)) {
            val (_, streamAfterColon) = stream.consume(TokenType.COLON)
            val (typeToken, streamAfterType) = streamAfterColon.consume(TokenType.TYPE)
            if (typeToken == null) {
                (null to SyntaxError("Se esperaba un tipo después de ':'")) to streamAfterColon
            } else {
                (typeToken.getValue() to null) to streamAfterType
            }
        } else {
            (null to null) to stream
        }
    }
}
