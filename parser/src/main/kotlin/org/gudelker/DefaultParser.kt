package org.gudelker

import org.example.Statement
import org.gudelker.components.org.gudelker.TokenType

class DefaultParser(val list: List<Token>, val root: List<Statement>) {
    fun parse(): List<Statement> {
        return parseRecursive(0, root)
    }

    private fun parseStatement(token: Token): Statement {
        // TODO: Implement actual statement parsing logic
        throw NotImplementedError("parseStatement not implemented")
    }
    private fun parseRecursive(index: Int, ast: List<Statement>): List<Statement> {
        if (index >= list.size || list[index].getType() == TokenType.EOF) {
            return ast
        }
        val token = list[index]
        if (token.getType() == TokenType.SEMICOLON) {
            return parseRecursive(index + 1, ast)
        }
        val statement = parseStatement(token)
        val newRoot = ast + statement
        return parseRecursive(index + 1, newRoot)
    }

    fun getRoot(): List<Statement> = root
}