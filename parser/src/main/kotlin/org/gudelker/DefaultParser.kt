package org.gudelker

import org.example.org.gudelker.Statement
import org.gudelker.components.org.gudelker.TokenType

import org.gudelker.result.Result
import org.gudelker.result.Valid
import org.gudelker.rule.SyntaxRule


class DefaultParser(private val tokens: List<Token>,
                    private val root: List<Statement>,
                    private val rules: List<SyntaxRule>
    ) {
    fun parse(): Result {
        return parseRecursive(0, root)
    }

    private fun parseRecursive(index: Int, ast: List<Statement>): Result {
        if (isTokenAtIndexEof(index)) {
            return Valid(ast)
        }

        // Find the next semicolon or EOF
        val nextSemiColonIndex : Int = (index until tokens.size).firstOrNull {
            tokens[it].getType() == TokenType.SEMICOLON} ?: tokens.size

        val statementTokens = tokens.subList(index, nextSemiColonIndex)
        
        for (rule in rules) {
            if (rule.matches(statementTokens, index)) {

            }
        }
        return parseRecursive(index + 1, ast) // ver lo del punto y coma

    }

    private fun isTokenAtIndexEof(index: Int) = tokens[index].getType() == TokenType.EOF



    fun getRoot(): List<Statement> = root
}