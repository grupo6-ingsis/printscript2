package org.gudelker.parser

import Parser
import org.gudelker.Statement
import org.gudelker.result.ParserResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.Valid
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.rule.SyntaxRule
import org.gudelker.tokenstream.TokenStream

class DefaultParser(
    private val tokenStream: TokenStream,
    private val root: List<Statement>,
    private val rules: List<SyntaxRule>,
) : Parser {
    override fun parse(tokenStream: TokenStream): ParserResult {
        return parseRecursive(tokenStream, emptyList())
    }

    private fun parseRecursive(
        currentStream: TokenStream,
        statements: List<Statement>,
    ): ParserResult {
        if (currentStream.isAtEnd()) {
            return Valid(statements)
        }

        for (rule in rules) {
            if (rule.matches(currentStream)) {
                val parseResult = rule.parse(currentStream)
                return when (parseResult.parserResult) {
                    is ValidStatementParserResult -> {
                        val newStatements = statements + parseResult.parserResult.getStatement()
                        parseRecursive(parseResult.tokenStream, newStatements)
                    }
                    else -> parseResult.parserResult
                }
            }
        }

        return ParserSyntaxError("No se encontró regla válida para token: ${currentStream.current()?.getValue()}")
    }
}
