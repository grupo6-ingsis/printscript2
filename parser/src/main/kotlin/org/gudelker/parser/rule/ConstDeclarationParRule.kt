package org.gudelker.parser.rule

import org.gudelker.expressions.CanBeCallStatement
import org.gudelker.parser.parsingtoken.TypeParseResult
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.statements.declarations.ConstDeclaration
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.token.Token
import org.gudelker.token.TokenType
import kotlin.toString

class ConstDeclarationParRule(
    private val keywords: Set<String>,
    private val expressionRule: SyntaxParRule,
) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        val current = tokenStream.current()
        return current?.getType() == TokenType.KEYWORD && keywords.contains(current.getValue())
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
        val typeCombo = createCombo(typeResult.typeName, typeResult.typePosition)
        val (assignToken, streamAfterAssign) = streamAfterType(typeResult)
        if (assignToken == null) {
            return errorResult("Se esperaba '=' después de la declaración", typeResult.nextStream)
        }
        val equalsCombo = createCombo(assignToken.getValue(), getPosition(assignToken))!!
        val colonCombo = createCombo(typeResult.colon, typeResult.colonPosition)

        return parseWithAssignment(
            createCombo(keywordToken.getValue(), keywordPos)!!,
            createCombo(identifierToken.getValue(), identifierPos)!!,
            typeCombo,
            streamAfterAssign,
            colonCombo,
            equalsCombo,
        )
    }

    private fun parseWithAssignment(
        keyword: ComboValuePosition<String>,
        identifier: ComboValuePosition<String>,
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
        if (expressionStatement !is CanBeCallStatement) {
            return errorResult("No se puede parsear esta expresión", expressionResult.tokenStream)
        }
        val (semicolonToken, finalStream) = expressionResult.tokenStream.consume(TokenType.SEMICOLON)
        if (semicolonToken == null) {
            return errorResult("Se esperaba un punto y coma al final de la declaración", expressionResult.tokenStream)
        }
        val statement =
            createConstDeclaration(
                keyword,
                identifier,
                colon,
                type,
                equals,
                expressionStatement,
            )

        return ParseResult(ValidStatementParserResult(statement), finalStream)
    }

    private fun createCombo(
        value: String?,
        pos: StatementPosition?,
    ): ComboValuePosition<String>? {
        return if (value != null && pos != null) ComboValuePosition(value, pos) else null
    }

    private fun createConstDeclaration(
        keyword: ComboValuePosition<String>,
        identifier: ComboValuePosition<String>,
        colon: ComboValuePosition<String>?,
        type: ComboValuePosition<String>?,
        equals: ComboValuePosition<String>,
        value: CanBeCallStatement,
    ): ConstDeclaration {
        return ConstDeclaration(
            keywordCombo = keyword,
            identifierCombo = identifier,
            colon = colon,
            type = type,
            equals = equals,
            value = value,
        )
    }
}
