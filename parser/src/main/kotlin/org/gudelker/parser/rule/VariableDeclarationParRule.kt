package org.gudelker.parser.rule

import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

class VariableDeclarationParRule(
    private val keywords: Set<String>,
    private val expressionRule: SyntaxParRule,
) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        val current = tokenStream.current()
        return current?.getType() == TokenType.KEYWORD && keywords.contains(current.getValue())
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val (keywordToken, streamAfterKeyword) =
            consumeKeyword(tokenStream)
                ?: return errorResult("Se esperaba una keyword al inicio de la declaración", tokenStream)
        val keywordTokenPosition = getPosition(keywordToken!!)
        val identifierResult = ParserUtils.parseIdentifier(streamAfterKeyword)
        if (identifierResult.identifier == null) {
            return errorResult("Se esperaba un identificador después de '${keywordToken.getValue()}'", streamAfterKeyword)
        }
        val identifierPos = getPosition(identifierResult.identifier)
        val typeResult = ParserUtils.parseOptionalType(identifierResult.nextStream)
        if (typeResult.error != null) {
            return errorResult(typeResult.error.toString(), typeResult.nextStream)
        }
        val (assignToken, streamAfterAssign) = typeResult.nextStream.consume(TokenType.ASSIGNATION)
        return if (assignToken != null) {
            parseWithAssignment(
                keywordToken,
                keywordTokenPosition,
                identifierResult.identifier,
                identifierPos,
                typeResult.typeName,
                streamAfterAssign,
            )
        } else {
            parseWithoutAssignment(
                keywordToken,
                keywordTokenPosition,
                identifierResult.identifier,
                identifierPos,
                typeResult.typeName,
                typeResult.nextStream,
            )
        }
    }

    private fun consumeKeyword(tokenStream: TokenStream) = tokenStream.consume(TokenType.KEYWORD).takeIf { it.first != null }

    private fun getPosition(token: Token): StatementPosition {
        val pos = token.getPosition()
        return StatementPosition(pos.startLine, pos.startColumn, pos.endLine, pos.endColumn)
    }

    private fun errorResult(
        message: String,
        stream: TokenStream,
    ) = ParseResult(ParserSyntaxError(message), stream)

    private fun parseWithAssignment(
        keywordToken: Token,
        position: StatementPosition,
        identifierToken: Token,
        identifierPos: StatementPosition,
        type: String?,
        streamAfterAssign: TokenStream,
    ): ParseResult {
        val expressionResult = expressionRule.parse(streamAfterAssign)
        if (expressionResult.parserResult !is ValidStatementParserResult) {
            return errorResult("Error al parsear la expresión", expressionResult.tokenStream)
        }
        val expressionStatement = expressionResult.parserResult.getStatement()
        if (expressionStatement !is CanBeCallStatement) {
            return errorResult("No se puede parsear esta expresión", expressionResult.tokenStream)
        }
        val (semicolonToken, finalStream) = expressionResult.tokenStream.consume(TokenType.SEMICOLON)
        if (semicolonToken == null) {
            return errorResult("Se esperaba un punto y coma al final de la declaración", expressionResult.tokenStream)
        }
        val statement =
            VariableDeclaration(
                keywordCombo = ComboValuePosition(keywordToken.getValue(), position),
                identifierCombo = ComboValuePosition(identifierToken.getValue(), identifierPos),
                type,
                value = expressionStatement,
            )
        return ParseResult(ValidStatementParserResult(statement), finalStream)
    }

    private fun parseWithoutAssignment(
        keywordToken: Token,
        position: StatementPosition,
        identifierToken: Token,
        identifierPos: StatementPosition,
        type: String?,
        streamAfterType: TokenStream,
    ): ParseResult {
        if (type == null) {
            return errorResult("Se esperaba un tipo o asignación después del identificador", streamAfterType)
        }
        val (semicolonToken, finalStream) = streamAfterType.consume(TokenType.SEMICOLON)
        if (semicolonToken == null) {
            return errorResult("Se esperaba un punto y coma al final de la declaración", streamAfterType)
        }
        val statement =
            VariableDeclaration(
                keywordCombo = ComboValuePosition(keywordToken.getValue(), position),
                identifierCombo = ComboValuePosition(identifierToken.getValue(), identifierPos),
                type,
                value = null,
            )
        return ParseResult(ValidStatementParserResult(statement), finalStream)
    }
}
