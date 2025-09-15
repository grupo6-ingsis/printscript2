package org.gudelker.parser.tokenstream

import org.gudelker.token.Token
import org.gudelker.token.TokenType

class TokenStream private constructor(
    private val tokens: List<Token>,
    private val currentIndex: Int = 0,
) {
    constructor(tokens: List<Token>) : this(tokens, 0)

    fun getTokens(): List<Token> = tokens

    fun current(): Token? {
        return if (currentIndex < tokens.size) tokens[currentIndex] else null
    }

    fun peek(offset: Int = 1): Token? {
        val peekIndex = currentIndex + offset
        return if (peekIndex < tokens.size) tokens[peekIndex] else null
    }

    fun next(): Pair<Token?, TokenStream> {
        val token = current()
        val newIndex = if (currentIndex < tokens.size) currentIndex + 1 else currentIndex
        return token to TokenStream(tokens, newIndex)
    }

    fun hasNext(): Boolean {
        return currentIndex < tokens.size
    }

    fun isAtEnd(): Boolean {
        return currentIndex >= tokens.size || current()?.getType() == TokenType.EOF
    }

    fun getCurrentIndex(): Int = currentIndex

    fun check(type: TokenType): Boolean {
        return current()?.getType() == type
    }

    fun consume(type: TokenType): Pair<Token?, TokenStream> {
        return if (check(type)) {
            val (token, newStream) = next()
            token to newStream
        } else {
            null to this
        }
    }
}
