package org.gudelker

import org.example.Statement
import org.gudelker.components.org.gudelker.TokenType

class DefaultParser(val list: List<Token>, val root: List<Statement>) {
    fun parse(): DefaultParser {
        fun parseRec(index: Int, acc: List<Statement>): DefaultParser {
            if (index >= list.size || list[index].getType() == TokenType.EOF) {
                return DefaultParser(list.subList(index, list.size), acc)
            }
            val token = list[index]
            // Replace with your actual statement parsing logic:
            if (token.getType() == TokenType.SEMICOLON) {
                return parseRec(index + 1, acc)
            }
            val statement = parseStatement(token) // Implement this function
            return parseRec(index + 1, acc + statement)
        }
        return parseRec(0, root)
    }

    private fun parseStatement(token: Token): Statement {
        // TODO: Implement actual statement parsing logic
        throw NotImplementedError("parseStatement not implemented")
    }

    fun getRoot(): List<Statement> = root
}