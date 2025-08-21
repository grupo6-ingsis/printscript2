package org.gudelker.rule

import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.Result
import kotlin.text.matches

class BinaryExpressionRule(private val expressionRule: SyntaxRule): SyntaxRule {
  override fun matches(tokens: List<Token>, index: Int): Boolean {
    // left expression
    if (!expressionRule.matches(tokens, index)) return false

    // 2. Operator
    val operatorIndex = index + 1
    if (operatorIndex >= tokens.size || tokens[operatorIndex].getType() != TokenType.OPERATOR) return false

    // 3. Right expression
    val rightExprIndex = operatorIndex + 1
    if (rightExprIndex >= tokens.size || !expressionRule.matches(tokens, rightExprIndex)) return false
    return true
  }

  override fun parse(tokens: List<Token>, index: Int): Result {
    TODO("Not yet implemented")
  }
}