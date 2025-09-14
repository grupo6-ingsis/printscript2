package org.gudelker.parser.rule

import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.ExpressionStatement
import org.gudelker.parser.parsingtoken.TypeParseResult
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.statements.declarations.ConstDeclaration
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

class ConstDeclarationParRule(
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
        val keywordPos = getPosition(keywordToken!!)
        val (identifierToken, streamAfterIdentifier) =
            consumeIdentifier(streamAfterKeyword)
                ?: return errorResult("Se esperaba un identificador después de '${keywordToken.getValue()}'", streamAfterKeyword)
        val identifierPos = getPosition(identifierToken!!)
        val typeResult = ParserUtils.parseOptionalType(streamAfterIdentifier)
        if (typeResult.error != null) {
            return errorResult(typeResult.error.toString(), typeResult.nextStream)
        }
        // Create type ComboValuePosition only if type is present
        val typeCombo =
            if (typeResult.typeName != null && typeResult.typePosition != null) {
                ComboValuePosition(typeResult.typeName, typeResult.typePosition)
            } else {
                null
            }
        val (assignToken, streamAfterAssign) = streamAfterType(typeResult)
        if (assignToken == null) {
            return errorResult("Se esperaba '=' después de la declaración", typeResult.nextStream)
        }
        val equalsPos = assignToken.getPosition()
        val equalsCombo =
            ComboValuePosition(
                assignToken.getValue(),
                StatementPosition(equalsPos.startLine, equalsPos.startColumn, equalsPos.endLine, equalsPos.endColumn),
            )

        val colonCombo =
            if (typeResult.colon != null) {
                ComboValuePosition(typeResult.colon, typeResult.colonPosition!!)
            } else {
                null
            }

        return parseWithAssignment(
            keywordToken,
            keywordPos,
            identifierToken,
            identifierPos,
            typeCombo,
            streamAfterAssign,
            colonCombo,
            equalsCombo,
        )
    }

    private fun consumeKeyword(tokenStream: TokenStream) = tokenStream.consume(TokenType.KEYWORD).takeIf { it.first != null }

    private fun consumeIdentifier(tokenStream: TokenStream) = tokenStream.consume(TokenType.IDENTIFIER).takeIf { it.first != null }

    private fun getPosition(token: Token): StatementPosition {
        val pos = token.getPosition()
        return StatementPosition(pos.startLine, pos.startColumn, pos.endLine, pos.endColumn)
    }

    private fun errorResult(
        message: String,
        stream: TokenStream,
    ) = ParseResult(ParserSyntaxError(message), stream)

    private fun streamAfterType(typeResult: TypeParseResult) = typeResult.nextStream.consume(TokenType.ASSIGNATION)

    private fun parseWithAssignment(
        keywordToken: Token,
        keywordPos: StatementPosition,
        identifierToken: Token,
        identifierPos: StatementPosition,
        type: ComboValuePosition<String>?,
        streamAfterAssign: TokenStream,
        colon: ComboValuePosition<String>?,
        equals: ComboValuePosition<String>,
    ): ParseResult {
        val expressionResult = expressionRule.parse(streamAfterAssign)
        if (expressionResult.parserResult !is ValidStatementParserResult) {
            return errorResult("Error al parsear la expresión", expressionResult.tokenStream)
        }
        val expressionStatement = expressionResult.parserResult.getStatement()
        if (expressionStatement !is ExpressionStatement) {
            return errorResult("No se puede parsear esta expresión", expressionResult.tokenStream)
        }
        val (semicolonToken, finalStream) = expressionResult.tokenStream.consume(TokenType.SEMICOLON)
        if (semicolonToken == null) {
            return errorResult("Se esperaba un punto y coma al final de la declaración", expressionResult.tokenStream)
        }
        val statement =
            ConstDeclaration(
                ComboValuePosition(keywordToken.getValue(), keywordPos),
                ComboValuePosition(identifierToken.getValue(), identifierPos),
                colon,
                type,
                equals,
                expressionStatement,
            )
        return ParseResult(ValidStatementParserResult(statement), finalStream)
    }
}
