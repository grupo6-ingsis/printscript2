package org.gudelker.parser

import Parser
import org.gudelker.result.ParserResult
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.Valid
import org.gudelker.result.ValidStatementParserResult
import org.gudelker.rule.SyntaxParRule
import org.gudelker.statements.interfaces.Statement
import org.gudelker.tokenstream.TokenStream

class DefaultParser(
    private val rules: List<SyntaxParRule>,
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
