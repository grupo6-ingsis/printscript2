package org.gudelker.rule

import org.gudelker.result.ParseResult
import org.gudelker.result.SyntaxError
import org.gudelker.tokenstream.TokenStream

class ExpressionRule(
    private val rules: List<SyntaxRule>,
) : SyntaxRule {
    override fun matches(tokenStream: TokenStream): Boolean {
        return rules.any { it.matches(tokenStream) }
    }

    override fun parse(tokenStream: TokenStream): ParseResult {
        val result = rules.firstOrNull { it.matches(tokenStream) }?.parse(tokenStream)
        if (result != null) {
            return result
        } else {
            return ParseResult(
                SyntaxError("No se encontró una expresión válida en la posición ${tokenStream.getCurrentIndex()}"),
                tokenStream,
            )
        }
    }
}
