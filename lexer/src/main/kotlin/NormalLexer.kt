package org.gudelker.lexer

import org.gudelker.components.Position
import org.gudelker.components.Token
import org.gudelker.components.TokenType

class NormalLexer : Lexer {
    override fun lex(input: String): List<Token> {
        var actualWord = ""
        var tokensList = mutableListOf<Token>()
        var startPos = Position()

        for (character: Char in input) { // ACORDARSE de ver el tema de leer de archivo o string (o si tenemos algo que pase de archivo a string poniendo \n en los saltos)
            if (character == ' ') {
                val type: TokenType = findTokenType(actualWord)
                val newPos = startPos.copy(endColumn = startPos.endColumn - 1)
                tokensList.add(Token(type, actualWord, newPos))
                actualWord = resetWordToEmpty()
            } else {
                actualWord = addCharacterToWord(actualWord, character)
            }
            startPos = advancePosition(startPos, character)
        }
        return tokensList
    }

    private fun resetWordToEmpty(): String {
        return ""
    }

    private fun addCharacterToWord(actualWord: String, character: Char): String {
        var newWord = actualWord
        newWord += character
        return newWord
    }

    private fun findTokenType(text: String): TokenType {
        TokenRegex.getAllPatterns().forEach {
            (type, pattern) -> if (text.matches(pattern.toRegex())) {
                return type
            }
        }
        return TokenType.UNKNOWN
    }

    private fun advancePosition( // No está bien implementado pero va por ahí
        position: Position,
        char: Char
    ): Position {
        return when (char) {
            '\n' -> position.copy(
                startLine = position.endLine + 1,
                startColumn = 1,
                endColumn = 1,
                endLine = position.endLine + 1
            )
            else -> position.copy(
                endColumn = position.endColumn + 1
            )
        }
    }
}