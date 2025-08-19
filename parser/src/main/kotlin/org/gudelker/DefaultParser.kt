package org.gudelker

import org.example.org.gudelker.Statement
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.Result
import org.gudelker.result.SyntaxError
import org.gudelker.result.Valid
import org.gudelker.result.ValidStatementResult
import org.gudelker.rule.SyntaxRule

class DefaultParser(
    private val tokens: List<Token>,
    private val root: List<Statement>,
    private val rules: List<SyntaxRule>,
) {
    fun parse(): Result {
        return parseRecursive(0, root)
    }

    private fun parseRecursive(
        index: Int,
        ast: List<Statement>,
    ): Result {
        if (isTokenAtIndexEof(index)) {
            return Valid(ast)
        }

        for (rule in rules) {
            if (rule.matches(tokens, index)) {
                val result = rule.parse(tokens, index)
                if (result is ValidStatementResult) {
                    val newAst = ast + result.getStatement()
                    return parseRecursive(result.getIndex(), newAst)
                } else {
                    return result // If the result is not valid, return it
                }
            }
        }

        return SyntaxError(".")
    }

    private fun isTokenAtIndexEof(index: Int) = tokens[index].getType() == TokenType.EOF

    fun getRoot(): List<Statement> = root
}
