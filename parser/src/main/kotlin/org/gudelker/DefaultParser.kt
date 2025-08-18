package org.gudelker

import org.example.org.gudelker.Statement
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.Result
import org.gudelker.result.Valid

class DefaultParser(val list: List<Token>, val root: List<Statement>) {
    fun parse(): Result{
        return parseRec(0, root)
    }

    private fun parseRec(index: Int, ast: List<Statement>): Result {
        if (index >= list.size || list[index].getType() == TokenType.EOF) {
            return Valid(ast)
        }
        // Find the next semicolon or EOF
        val end = (index until list.size).firstOrNull {
            list[it].getType() == TokenType.SEMICOLON || list[it].getType() == TokenType.EOF
        } ?: list.size
        val statementTokens = list.subList(index, end)
        val statement = parseStatement(statementTokens)
        val nextIndex = if (end < list.size && list[end].getType() == TokenType.SEMICOLON) end + 1 else end
        return parseRec(nextIndex, ast + statement)
    }

    // Now parseStatement takes a list of tokens
    private fun parseStatement(tokens: List<Token>): Statement {
        if(tokens.get(0).getType() == TokenType.KEYWORD) {

        }
        TODO()
    }

    private fun createVariableDeclaration(tokens: List<Token>): Statement {
TODO()
    }

    fun getRoot(): List<Statement> = root
}