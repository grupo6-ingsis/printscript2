package org.gudelker.parser.rule

import org.gudelker.expressions.BooleanExpressionStatement
import org.gudelker.expressions.ConditionalExpression
import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition
import org.gudelker.stmtposition.StatementPosition
import org.gudelker.token.Token
import org.gudelker.token.TokenType

class ConditionalParRule(
    private val booleanExpressionRule: SyntaxParRule,
    private val statementRules: List<SyntaxParRule>,
) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return tokenStream.current()?.getType() == TokenType.IF_KEYWORD
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val (ifToken, afterIf) = tokenStream.consume(TokenType.IF_KEYWORD)
        if (ifToken == null) return ParseResult(ParserSyntaxError("Se esperaba 'if'"), tokenStream)
        val tokenPosition = ParserUtils.createStatementPosition(ifToken)
        val (booleanExpr, afterCondition, parenCombos) = parseBooleanCondition(afterIf)
        if (booleanExpr == null || parenCombos == null) {
            return ParseResult(ParserSyntaxError("Error al parsear la condición booleana"), afterIf)
        }
        val (ifBody, afterIfBody, ifBrackets) = parseBlock(afterCondition)
        if (ifBody == null || ifBrackets == null) {
            return ParseResult(ParserSyntaxError("Error al parsear el bloque if"), afterCondition)
        }
        val currentToken = afterIfBody.current()
        return if (currentToken?.getType() == TokenType.ELSE_KEYWORD) {
            parseElseBlock(
                afterIfBody, ifToken, tokenPosition, booleanExpr, ifBody,
                ifBrackets.first, ifBrackets.second, parenCombos.second, parenCombos.first,
            )
        } else {
            val conditional =
                createConditionalExpression(
                    ifToken, tokenPosition, booleanExpr, ifBody, parenCombos.first, parenCombos.second,
                    null, ifBrackets.first, ifBrackets.second, null, null, null,
                )
            ParseResult(ValidStatementParserResult(conditional), afterIfBody)
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
        closeParenthesis: ComboValuePosition<String>,
        openCloseParenthesis: ComboValuePosition<String>,
    ): ParseResult {
        val (elseToken, streamAfterElse) = parseElseKeyword(tokenStream)
        if (elseToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba 'else'"), tokenStream)
        }
        val elseKeyword = ComboValuePosition(elseToken.getValue(), ParserUtils.createStatementPosition(elseToken))
        val (openBracketToken, streamAfterOpenBracket) = parseElseOpenBracket(streamAfterElse)
        if (openBracketToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '{' después de 'else'"), streamAfterElse)
        }
        val elseOpenBracketCombo = ComboValuePosition(openBracketToken.getValue(), ParserUtils.createStatementPosition(openBracketToken))
        val (elseBody, streamAfterElseBody) = parseElseStatements(streamAfterOpenBracket)
        val (closeBracketToken, streamAfterCloseBracket) = parseElseCloseBracket(streamAfterElseBody)
        if (closeBracketToken == null) {
            return ParseResult(ParserSyntaxError("Se esperaba '}' para cerrar el bloque else"), streamAfterElseBody)
        }
        val elseCloseBracketCombo = ComboValuePosition(closeBracketToken.getValue(), ParserUtils.createStatementPosition(closeBracketToken))
        val conditional =
            createConditionalExpression(
                ifToken, position, booleanExpression, ifBody, openCloseParenthesis, closeParenthesis,
                elseBody, ifOpenBracket, ifCloseBracket, elseKeyword, elseOpenBracketCombo, elseCloseBracketCombo,
            )
        return ParseResult(ValidStatementParserResult(conditional), streamAfterCloseBracket)
    }

    private fun parseParenthesis(
        tokenStream: TokenStream,
    ): Triple<BooleanExpressionStatement?, TokenStream, Pair<ComboValuePosition<String>, ComboValuePosition<String>>?> {
        val (openParenToken, afterOpenParen) = tokenStream.consume(TokenType.OPEN_PARENTHESIS)
        if (openParenToken == null) return Triple(null, tokenStream, null)
        val openCombo = ComboValuePosition(openParenToken.getValue(), ParserUtils.createStatementPosition(openParenToken))
        val booleanResult = booleanExpressionRule.parse(afterOpenParen)
        val booleanExpr = (booleanResult.parserResult as? ValidStatementParserResult)?.getStatement() as? BooleanExpressionStatement
        if (booleanExpr == null) return Triple(null, afterOpenParen, null)
        val (closeParenToken, afterCloseParen) = booleanResult.tokenStream.consume(TokenType.CLOSE_PARENTHESIS)
        if (closeParenToken == null) return Triple(null, booleanResult.tokenStream, null)
        val closeCombo = ComboValuePosition(closeParenToken.getValue(), ParserUtils.createStatementPosition(closeParenToken))
        return Triple(booleanExpr, afterCloseParen, openCombo to closeCombo)
    }

    private fun parseBlock(
        tokenStream: TokenStream,
    ): Triple<List<Statement>?, TokenStream, Pair<ComboValuePosition<String>, ComboValuePosition<String>>?> {
        val (openBracketToken, afterOpenBracket) = tokenStream.consume(TokenType.OPEN_BRACKET)
        if (openBracketToken == null) return Triple(null, tokenStream, null)
        val openCombo = ComboValuePosition(openBracketToken.getValue(), ParserUtils.createStatementPosition(openBracketToken))
        val (body, afterBody) = parseStatements(afterOpenBracket)
        val (closeBracketToken, afterCloseBracket) = afterBody.consume(TokenType.CLOSE_BRACKET)
        if (closeBracketToken == null) return Triple(null, afterBody, null)
        val closeCombo = ComboValuePosition(closeBracketToken.getValue(), ParserUtils.createStatementPosition(closeBracketToken))
        return Triple(body, afterCloseBracket, openCombo to closeCombo)
    }

    private fun parseBooleanCondition(
        tokenStream: TokenStream,
    ): Triple<BooleanExpressionStatement?, TokenStream, Pair<ComboValuePosition<String>, ComboValuePosition<String>>?> {
        return parseParenthesis(tokenStream)
    }

    private fun parseElseKeyword(tokenStream: TokenStream): Pair<Token?, TokenStream> {
        return tokenStream.consume(TokenType.ELSE_KEYWORD)
    }

    private fun parseElseOpenBracket(tokenStream: TokenStream): Pair<Token?, TokenStream> {
        return tokenStream.consume(TokenType.OPEN_BRACKET)
    }

    private fun parseElseStatements(tokenStream: TokenStream): Pair<List<Statement>, TokenStream> {
        return parseStatements(tokenStream)
    }

    private fun parseElseCloseBracket(tokenStream: TokenStream): Pair<Token?, TokenStream> {
        return tokenStream.consume(TokenType.CLOSE_BRACKET)
    }

    private fun createConditionalExpression(
        ifToken: Token,
        position: StatementPosition,
        booleanExpression: BooleanExpressionStatement,
        ifBody: List<Statement>,
        openParenthesisCombo: ComboValuePosition<String>,
        closeParenthesisCombo: ComboValuePosition<String>,
        elseBody: List<Statement>?,
        ifOpenBracketCombo: ComboValuePosition<String>?,
        ifCloseBracketCombo: ComboValuePosition<String>?,
        elseKeyword: ComboValuePosition<String>?,
        elseOpenBracketCombo: ComboValuePosition<String>?,
        elseCloseBracketCombo: ComboValuePosition<String>?,
    ): ConditionalExpression {
        return ConditionalExpression(
            ComboValuePosition(ifToken.getValue(), position),
            booleanExpression,
            ifBody,
            openParenthesisCombo,
            closeParenthesisCombo,
            elseBody,
            ifOpenBracketCombo,
            ifCloseBracketCombo,
            elseKeyword,
            elseOpenBracketCombo,
            elseCloseBracketCombo,
        )
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
