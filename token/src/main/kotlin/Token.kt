package org.gudelker.components

data class Token(
    private val type: TokenType,
    private val value: String,
    private val position: Position
) {
    fun getType(): TokenType {
        return type
    }
    fun getValue(): String {
        return value
    }
    fun getPosition(): Position {
        return position
    }
    fun copy(
        position: Position = this.position,
        type: TokenType = this.type,
        value: String = this.value
    ) : Token {
        return Token(type, value, position)
    }
}