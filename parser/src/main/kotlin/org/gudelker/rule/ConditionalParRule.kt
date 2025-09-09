package org.gudelker.rule

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.BooleanExpressionStatement
import org.gudelker.expressions.ConditionalExpression
import org.gudelker.result.ParseResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.tokenstream.TokenStream

class ConditionalParRule(
    private val booleanExpressionRule: SyntaxParRule,
    private val statementRules: List<SyntaxParRule>,
) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.IF_KEYWORD
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        // Consume 'if'
        val (ifToken, streamAfterIf) = tokenStream.consume(TokenType.IF_KEYWORD)
        if (ifToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba 'if'"), tokenStream)
        }
        val tokenPosition = ifToken.getPosition()
        val position =
            StatementPosition(
                tokenPosition.startLine,
                tokenPosition.startColumn,
                tokenPosition.endLine,
                tokenPosition.endColumn,
            )

        // Consume '('
        val (openParenToken, streamAfterOpenParen) = streamAfterIf.consume(TokenType.OPEN_PARENTHESIS)
        if (openParenToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '(' después de 'if'"), streamAfterIf)
        }

        // Parse boolean expression
        val booleanResult = booleanExpressionRule.parse(streamAfterOpenParen)
        if (booleanResult.parserResult !is ValidStatementParserResult) {
            return ParseResult(ParserSyntaxError("Error al parsear la condición booleana"), booleanResult.tokenStream)
        }
        val booleanExpression = booleanResult.parserResult.getStatement() as BooleanExpressionStatement

        // Consume ')'
        val (closeParenToken, streamAfterCloseParen) = booleanResult.tokenStream.consume(TokenType.CLOSE_PARENTHESIS)
        if (closeParenToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba ')' después de la condición"), booleanResult.tokenStream)
        }

        // Consume '{'
        val (openBracketToken, streamAfterOpenBracket) = streamAfterCloseParen.consume(TokenType.OPEN_BRACKET)
        if (openBracketToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '{' después de la condición"), streamAfterCloseParen)
        }

        // Parse if body statements
        val (ifBody, streamAfterIfBody) = parseStatements(streamAfterOpenBracket)

        // Consume '}'
        val (closeBracketToken, streamAfterCloseBracket) = streamAfterIfBody.consume(TokenType.CLOSE_BRACKET)
        if (closeBracketToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '}' para cerrar el bloque if"), streamAfterIfBody)
        }

        // Check for optional 'else'
        val currentToken = streamAfterCloseBracket.current()
        return if (currentToken?.getType() == TokenType.ELSE_KEYWORD) {
            parseElseBlock(streamAfterCloseBracket, ifToken, position, booleanExpression, ifBody)
        } else {
            val conditional =
                ConditionalExpression(
                    ComboValuePosition(ifToken.getValue(), position),
                    booleanExpression,
                    ifBody,
                    null,
                )
            ParseResult(ValidStatementParserResult(conditional), streamAfterCloseBracket)
        }
    }

    private fun parseElseBlock(
        tokenStream: TokenStream,
        ifToken: org.gudelker.Token,
        position: StatementPosition,
        booleanExpression: BooleanExpressionStatement,
        ifBody: List<Statement>,
    ): ParseResult {
        // Consume 'else'
        val (elseToken, streamAfterElse) = tokenStream.consume(TokenType.ELSE_KEYWORD)
        if (elseToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba 'else'"), tokenStream)
        }

        // Consume '{'
        val (openBracketToken, streamAfterOpenBracket) = streamAfterElse.consume(TokenType.OPEN_BRACKET)
        if (openBracketToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '{' después de 'else'"), streamAfterElse)
        }

        // Parse else body statements
        val (elseBody, streamAfterElseBody) = parseStatements(streamAfterOpenBracket)

        // Consume '}'
        val (closeBracketToken, streamAfterCloseBracket) = streamAfterElseBody.consume(TokenType.CLOSE_BRACKET)
        if (closeBracketToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '}' para cerrar el bloque else"), streamAfterElseBody)
        }

        val conditional =
            ConditionalExpression(
                ComboValuePosition(ifToken.getValue(), position),
                booleanExpression,
                ifBody,
                elseBody,
            )
        return ParseResult(ValidStatementParserResult(conditional), streamAfterCloseBracket)
    }

    private fun parseStatements(tokenStream: TokenStream): Pair<List<Statement>, TokenStream> {
        return parseStatementsRecursive(tokenStream, emptyList())
    }

    private fun parseStatementsRecursive(
        tokenStream: TokenStream,
        accumulator: List<Statement>,
    ): Pair<List<Statement>, TokenStream> {
        if (tokenStream.isAtEnd() || tokenStream.current()?.getType() == TokenType.CLOSE_BRACKET) {
            return accumulator to tokenStream
        }

        val matchingRule = statementRules.firstOrNull { it.matches(tokenStream) }
        return if (matchingRule != null) {
            val parseResult = matchingRule.parse(tokenStream)
            if (parseResult.parserResult is ValidStatementParserResult) {
                val newAccumulator = accumulator + parseResult.parserResult.getStatement()
                parseStatementsRecursive(parseResult.tokenStream, newAccumulator)
            } else {
                accumulator to tokenStream
            }
        } else {
            accumulator to tokenStream
        }
    }
}
