package org.gudelker.parser.rule

import org.gudelker.parser.result.ParseResult
import org.gudelker.parser.result.ParserSyntaxError
import org.gudelker.parser.tokenstream.TokenStream

class ExpressionParRule(
    private val rules: List<SyntaxParRule>,
) : SyntaxParRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return rules.any { it.matches(tokenStream) }
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val result = rules.firstOrNull { it.matches(tokenStream) }?.parse(tokenStream)
        if (result != null) {
            return result
        } else {
            return ParseResult(
                ParserSyntaxError("No se encontró una expresión válida en la posición ${tokenStream.getCurrentIndex()}"),
                tokenStream,
            )
        }
    }

    fun getRules(): List<SyntaxParRule> {
        return rules
    }
}
