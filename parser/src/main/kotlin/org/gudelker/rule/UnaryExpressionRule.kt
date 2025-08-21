package org.gudelker.rule

import org.gudelker.Token
import org.gudelker.result.Result

class UnaryExpressionRule(private val simpleExpressionRule: SyntaxRule): SyntaxRule {
  override fun matches(tokens: List<Token>, index: Int): Boolean {
    return simpleExpressionRule.matches(tokens, index)
  }

  override fun parse(tokens: List<Token>, index: Int): Result {
    TODO("Not yet implemented")
  }
}