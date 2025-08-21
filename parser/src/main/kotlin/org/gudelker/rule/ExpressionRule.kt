package org.gudelker.rule

import org.example.org.gudelker.LiteralNumber
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.Result
import org.gudelker.result.SyntaxError
import org.gudelker.result.ValidStatementResult

class ExpressionRule(private val subRules: List<SyntaxRule>) : SyntaxRule {
  override fun matches(
    tokens: List<Token>,
    index: Int,
  ): Boolean {
    if (index >= tokens.size) return false
    return subRules.any { rule -> rule.matches(tokens, index) }
  }

  override fun parse(
    tokens: List<Token>,
    index: Int,
  ): Result {
    if (index >= tokens.size) {
      return SyntaxError("Índice fuera de rango: $index")
    }

    val token = tokens[index]
    if (token.getType() != TokenType.NUMBER) {
      return SyntaxError("Se esperaba un número en la posición $index")
    }

    // Procesar el número literal
    val value = token.getValue().toInt()
    val literalNumber = LiteralNumber(value)

    // Devolver el resultado indicando que hemos consumido un token
    return ValidStatementResult(literalNumber, index + 1)
  }
}
