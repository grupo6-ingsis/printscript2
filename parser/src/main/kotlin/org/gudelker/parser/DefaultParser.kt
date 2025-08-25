package org.gudelker.parser

import Parser
import org.gudelker.Statement
import org.gudelker.result.Result
import org.gudelker.result.SyntaxError
import org.gudelker.result.Valid
import org.gudelker.result.ValidStatementResult
import org.gudelker.rule.SyntaxRule
import org.gudelker.tokenstream.TokenStream

class DefaultParser(
    private val tokenStream: TokenStream,
    private val root: List<Statement>,
    private val rules: List<SyntaxRule>,
) : Parser {
    override fun parse(tokenStream: TokenStream): Result {
        return parseRecursive(tokenStream, emptyList())
    }

    private fun parseRecursive(
        currentStream: TokenStream,
        statements: List<Statement>,
    ): Result {
        if (currentStream.isAtEnd()) {
            return Valid(statements)
        }

        for (rule in rules) {
            if (rule.matches(currentStream)) {
                val parseResult = rule.parse(currentStream)
                return when (parseResult.result) {
                    is ValidStatementResult -> {
                        val newStatements = statements + parseResult.result.getStatement()
                        parseRecursive(parseResult.tokenStream, newStatements)
                    }
                    else -> parseResult.result
                }
            }
        }

        return SyntaxError("No se encontró regla válida para token: ${currentStream.current()?.getValue()}")
    }

    fun getRoot(): List<Statement> = root
}
