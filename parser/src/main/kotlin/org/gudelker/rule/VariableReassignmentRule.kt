package org.gudelker.rule

import org.gudelker.ExpressionStatement
import org.gudelker.VariableReassignment
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.tokenstream.TokenStream

class VariableReassignmentRule(
    private val expressionRule: SyntaxRule,
) : SyntaxRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        val current = tokenStream.current()
        // Verifica que sea un identificador seguido de '='
        if (current?.getType() != TokenType.IDENTIFIER) return false

        val next = tokenStream.peek(1)
        return next?.getType() == TokenType.ASSIGNATION
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        // Consume identifier
        val (identifierToken, streamAfterIdentifier) = tokenStream.consume(TokenType.IDENTIFIER)
        if (identifierToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba un identificador"), tokenStream)
        }

        // Assignation
        val (assignToken, streamAfterAssign) = streamAfterIdentifier.consume(TokenType.ASSIGNATION)
        if (assignToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '=' después del identificador"), streamAfterIdentifier)
        }

        // Expression
        val expressionResult = expressionRule.parse(streamAfterAssign)
        if (expressionResult.parserResult !is ValidStatementParserResult) {
            return ParseResult(ParserSyntaxError("Error al parsear la expresión"), expressionResult.tokenStream)
        }

        val expressionStatement = expressionResult.parserResult.getStatement() as ExpressionStatement

        // Semicolon
        val (semicolonToken, finalStream) = expressionResult.tokenStream.consume(TokenType.SEMICOLON)
        if (semicolonToken == null) {
            return ParseResult(
                ParserSyntaxError("Se esperaba un punto y coma al final de la reasignación"),
                expressionResult.tokenStream,
            )
        }

        // Crear VariableReassignment
        val statement = VariableReassignment(identifierToken.getValue(), expressionStatement)
        return ParseResult(ValidStatementParserResult(statement), finalStream)
    }
}
