package org.gudelker.rule

import org.example.org.gudelker.LiteralNumber
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.Result
import org.gudelker.result.ValidStatementResult

class LiteralNumberRule : SyntaxRule {
  override fun matches(
    tokens: List<Token>,
    index: Int,
  ): Boolean {
    return tokens[index].getType() == TokenType.NUMBER
  }

  override fun parse(
    tokens: List<Token>,
    index: Int,
  ): Result {
    val value = tokens[index].getValue()
    val number = if (value.contains('.')) value.toFloat() else value.toInt()
    val currentIndex = index + 1
    return ValidStatementResult(
      LiteralNumber(number),
      currentIndex,
    )
  }
}
