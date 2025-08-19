package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType

class DoubleTokenizer : RuleTokenizer {
  override fun matches(
    actualWord: String,
    nextChar: Char?,
  ): Boolean {
    val doublePattern = "\\d+\\.\\d+".toRegex()

    return actualWord.matches(doublePattern) &&
      (
        nextChar == null || nextChar == '=' || nextChar == '>' ||
          nextChar == '<' || nextChar == ';' || nextChar == ')' ||
          nextChar == ',' || nextChar == ' ' || nextChar == '\n' ||
          nextChar == '\t'
      )
  }

  override fun generateToken(
    tokens: List<Token>,
    actualWord: String,
    position: Position,
  ): List<Token> {
    val newList = tokens + Token(TokenType.NUMBER, actualWord, position)
    return newList
  }
}
