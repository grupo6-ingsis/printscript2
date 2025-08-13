package org.gudelker

import org.gudelker.components.org.gudelker.TokenType

class NormalLexer(val rules : List<RuleTokenizer>) : Lexer {

    override fun lex(fileName: String): List<Token> {
        val reader = Reader(fileName)
        var actualWord = ""
        var tokensList = listOf<Token>()
        var startPos = Position()

        while (!reader.isEOF()) {
            actualWord += reader.next().toString()
            val nextChar = reader.peek()
            if (nextChar == '\n'){
                advancePosition(startPos, nextChar)
            }

            for (rule in rules) {
                if (rule.matches(actualWord, nextChar)) {
                    val pos = startPos.copy()
                    tokensList = rule.generateToken(tokensList, actualWord, pos)
                    startPos = advancePosition(startPos, nextChar)
                    actualWord = resetWordToEmpty()
                    break
                }
            }

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
        char: Char?
    ): Position {
        return when (char) {
            '\n' -> position.copy(
                startLine = position.endLine + 1,
                startColumn = 1,
                endColumn = 1,
                endLine = position.endLine + 1
            )
            else -> position.copy(
                endColumn = position.endColumn + 1,
                startColumn = position.endColumn

            )
        }
    }
}