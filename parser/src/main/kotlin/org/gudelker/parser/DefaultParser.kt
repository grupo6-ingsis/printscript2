package org.gudelker.parser

import Parser
import org.gudelker.parser.result.ParserResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.result.Valid
import org.gudelker.parser.result.ValidStatementParserResult
import org.gudelker.parser.rule.SyntaxParRule
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.statements.interfaces.Statement

class DefaultParser(
    private val rules: List<SyntaxParRule>,
) : Parser {
    override fun parse(tokenStream: TokenStream): ParserResult {
        return parseRecursive(tokenStream, emptyList())
    }

    public fun getRules(): List<SyntaxParRule> = rules

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
