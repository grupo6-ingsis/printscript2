package org.gudelker

interface RuleTokenizer {
  fun matches(
    actualWord: String,
    nextChar: Char?,
  ): Boolean

  fun generateToken(
    tokens: List<Token>,
    actualWord: String,
    position: Position,
  ): List<Token>
}
