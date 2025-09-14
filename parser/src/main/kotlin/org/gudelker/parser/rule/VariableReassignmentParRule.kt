package org.gudelker.parser.rule

import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.ExpressionStatement
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.statements.VariableReassignment
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

class VariableReassignmentParRule(
    private val expressionRule: SyntaxParRule,
) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        val current = tokenStream.current()
        val next = tokenStream.peek(1)
        return current?.getType() == TokenType.IDENTIFIER && next?.getType() == TokenType.ASSIGNATION
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val (identifierToken, streamAfterIdentifier) =
            consumeIdentifier(tokenStream)
                ?: return errorResult("Se esperaba un identificador", tokenStream)
        val identifierPos = getPosition(identifierToken!!)
        val (assignToken, streamAfterAssign) =
            consumeAssignation(streamAfterIdentifier)
                ?: return errorResult("Se esperaba '=' después del identificador", streamAfterIdentifier)
        return parseWithAssignment(identifierToken, identifierPos, streamAfterAssign)
    }

    private fun consumeIdentifier(tokenStream: TokenStream) = tokenStream.consume(TokenType.IDENTIFIER).takeIf { it.first != null }

    private fun consumeAssignation(tokenStream: TokenStream) = tokenStream.consume(TokenType.ASSIGNATION).takeIf { it.first != null }

    private fun getPosition(token: Token): StatementPosition {
        val pos = token.getPosition()
        return StatementPosition(pos.startLine, pos.startColumn, pos.endLine, pos.endColumn)
    }

    private fun errorResult(
        message: String,
        stream: TokenStream,
    ) = ParseResult(ParserSyntaxError(message), stream)

    private fun parseWithAssignment(
        identifierToken: Token,
        identifierPos: StatementPosition,
        streamAfterAssign: TokenStream,
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
            return errorResult("Se esperaba un punto y coma al final de la reasignación", expressionResult.tokenStream)
        }
        val statement =
            VariableReassignment(
                ComboValuePosition(identifierToken.getValue(), identifierPos),
                expressionStatement,
            )
        return ParseResult(ValidStatementParserResult(statement), finalStream)
    }
}
