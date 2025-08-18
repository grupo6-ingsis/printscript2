package org.gudelker

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.Result
import org.gudelker.result.SyntaxError
import org.gudelker.result.Valid

class FileLexer(private val rules: List<RuleTokenizer>) : Lexer {

    override fun lex(fileName: String): Result {
        val reader = Reader(fileName)

        fun lexRecursive(
            actualWord: String,
            tokensList: List<Token>,
            startPos: Position
        ): Result {
            if (reader.isEOF()) {
                val finalTokens = tokensList + Token(TokenType.EOF, "EOF", startPos)
                return Valid(finalTokens)
            }
            val newChar = reader.next().toString()
            val updatedWord = actualWord + newChar
            val nextChar = reader.peek()

            val updatedPos = if (nextCharIsNewLine(nextChar)) {
                advancePosition(startPos, nextChar)
            } else startPos

            val matchingRule = rules.firstOrNull { it.matches(updatedWord, nextChar) }

            return if (matchingRule != null) {
                val posWithNewOffset = changingOffSet(updatedPos, updatedWord)
                val pos = posWithNewOffset.copy()
                val newTokens = matchingRule.generateToken(tokensList, updatedWord, pos)
                val lastToken = newTokens.last()
                if (lastToken.getType() == TokenType.UNKNOWN) {
                    SyntaxError("Syntax error in line: ${lastToken.getPosition().startLine}")
                } else {
                    lexRecursive("", newTokens, advancePosition(posWithNewOffset, nextChar))
                }
            } else {
                lexRecursive(updatedWord, tokensList, updatedPos)
            }
        }

        return lexRecursive("", listOf(), Position())
    }

    private fun advancePosition(
        position: Position,
        char: Char?
    ): Position {
        return when (char) {
            '\n', '\r' -> position.copy(
                endOffset = -1,
                startLine = position.endLine + 1,
                startColumn = 0,
                endColumn = 0,
                endLine = position.endLine + 1
            )
            else -> position.copy(
                startOffset = position.endOffset + 1,
                endOffset = position.endOffset + 1,
                endColumn = position.endColumn + 1,
                startColumn = position.endColumn + 1
            )
        }
    }

    private fun changingOffSet(position: Position, actualWord: String): Position {
        return position.copy(
            endOffset = position.endOffset + actualWord.length,
        )
    }

    private fun nextCharIsNewLine(nextChar: Char?) = nextChar == '\n' || nextChar == '\r'

}