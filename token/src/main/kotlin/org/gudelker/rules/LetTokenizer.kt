package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType

class LetTokenizer : RuleTokenizer {
  override fun matches(
    actualWord: String,
    nextChar: Char?,
  ): Boolean {
    return actualWord == "let" && (nextChar == null || nextChar.isWhitespace())
  }

  override fun generateToken(
    tokens: List<Token>,
    actualWord: String,
    position: Position,
  ): List<Token> {
    val newList = tokens + Token(TokenType.KEYWORD, actualWord, position)
    return newList
  }
}
