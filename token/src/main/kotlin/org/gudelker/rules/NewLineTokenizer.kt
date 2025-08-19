package org.gudelker.rules

import org.gudelker.Position
import org.gudelker.RuleTokenizer
import org.gudelker.Token

class NewLineTokenizer : RuleTokenizer {
  override fun matches(
    actualWord: String,
    nextChar: Char?,
  ): Boolean = actualWord == "\n" || actualWord == "\r"

  override fun generateToken(
    tokens: List<Token>,
    actualWord: String,
    position: Position,
  ): List<Token> = tokens
}
