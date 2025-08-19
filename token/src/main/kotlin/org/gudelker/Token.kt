package org.gudelker

import org.gudelker.components.org.gudelker.TokenType

data class Token(
  private val type: TokenType,
  private val value: String,
  private val position: Position,
) {
  fun getType(): TokenType {
    return type
  }

  fun getValue(): String = value

  fun getPosition(): Position = position

  fun copy(
    position: Position = this.position,
    type: TokenType = this.type,
    value: String = this.value,
  ): Token = Token(type, value, position)
}
