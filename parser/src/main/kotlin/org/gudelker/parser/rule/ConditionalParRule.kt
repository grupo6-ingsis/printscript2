package org.gudelker.parser.rule

import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.expressions.BooleanExpressionStatement
import org.gudelker.expressions.ConditionalExpression
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition

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

        val openBracketPos = openBracketToken.getPosition()
        val openBracePosition =
            StatementPosition(
                openBracketPos.startLine,
                openBracketPos.startColumn,
                openBracketPos.endLine,
                openBracketPos.endColumn,
            )
        val ifOpenBracketCombo = ComboValuePosition(openBracketToken.getValue(), openBracePosition)

        // Parse if body statements
        val (ifBody, streamAfterIfBody) = parseStatements(streamAfterOpenBracket)

        // Consume '}'
        val (closeBracketToken, streamAfterCloseBracket) = streamAfterIfBody.consume(TokenType.CLOSE_BRACKET)
        if (closeBracketToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '}' para cerrar el bloque if"), streamAfterIfBody)
        }
        val closeBracketPos = closeBracketToken.getPosition()
        val closeBracketPosition =
            StatementPosition(
                closeBracketPos.startLine,
                closeBracketPos.startColumn,
                closeBracketPos.endLine,
                closeBracketPos.endColumn,
            )
        val ifCloseBracketCombo = ComboValuePosition(closeBracketToken.getValue(), closeBracketPosition)

        // Check for optional 'else'
        val currentToken = streamAfterCloseBracket.current()
        return if (currentToken?.getType() == TokenType.ELSE_KEYWORD) {
            parseElseBlock(streamAfterCloseBracket, ifToken, position, booleanExpression, ifBody, ifOpenBracketCombo, ifCloseBracketCombo)
        } else {
            val conditional =
                ConditionalExpression(
                    ComboValuePosition(ifToken.getValue(), position),
                    booleanExpression,
                    ifBody,
                    null,
                    ifOpenBracketCombo,
                    ifCloseBracketCombo,
                    null,
                    null,
                    null,
                )
            ParseResult(ValidStatementParserResult(conditional), streamAfterCloseBracket)
        }
    }

    private fun parseElseBlock(
        tokenStream: TokenStream,
        ifToken: Token,
        position: StatementPosition,
        booleanExpression: BooleanExpressionStatement,
        ifBody: List<Statement>,
        ifOpenBracket: ComboValuePosition<String>?,
        ifCloseBracket: ComboValuePosition<String>?,
    ): ParseResult {
        // Consume 'else'
        val (elseToken, streamAfterElse) = tokenStream.consume(TokenType.ELSE_KEYWORD)
        if (elseToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba 'else'"), tokenStream)
        }
        val elseTokenPos = elseToken.getPosition()
        val elsePosition =
            StatementPosition(
                elseTokenPos.startLine,
                elseTokenPos.startColumn,
                elseTokenPos.endLine,
                elseTokenPos.endColumn,
            )
        val elseKeyword = ComboValuePosition(elseToken.getValue(), elsePosition)

        // Consume '{'
        val (openBracketToken, streamAfterOpenBracket) = streamAfterElse.consume(TokenType.OPEN_BRACKET)
        if (openBracketToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '{' después de 'else'"), streamAfterElse)
        }
        val openBracketPos = openBracketToken.getPosition()
        val openBracketPosition =
            StatementPosition(
                openBracketPos.startLine,
                openBracketPos.startColumn,
                openBracketPos.endLine,
                openBracketPos.endColumn,
            )
        val elseOpenBracketCombo = ComboValuePosition(openBracketToken.getValue(), openBracketPosition)

        // Parse else body statements
        val (elseBody, streamAfterElseBody) = parseStatements(streamAfterOpenBracket)

        // Consume '}'
        val (closeBracketToken, streamAfterCloseBracket) = streamAfterElseBody.consume(TokenType.CLOSE_BRACKET)
        if (closeBracketToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '}' para cerrar el bloque else"), streamAfterElseBody)
        }
        val closeBracketPos = closeBracketToken.getPosition()
        val closeBracketPosition =
            StatementPosition(
                closeBracketPos.startLine,
                closeBracketPos.startColumn,
                closeBracketPos.endLine,
                closeBracketPos.endColumn,
            )
        val elseCloseBracketCombo = ComboValuePosition(openBracketToken.getValue(), closeBracketPosition)

        val conditional =
            ConditionalExpression(
                ComboValuePosition(ifToken.getValue(), position),
                booleanExpression,
                ifBody,
                elseBody,
                ifOpenBracket,
                ifCloseBracket,
                elseKeyword,
                elseOpenBracketCombo,
                elseCloseBracketCombo,
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
