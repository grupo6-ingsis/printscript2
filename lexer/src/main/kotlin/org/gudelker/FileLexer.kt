package org.gudelker

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.Result
import org.gudelker.result.SyntaxError
import org.gudelker.result.Valid

class FileLexer(val rules : List<RuleTokenizer>) : Lexer {

    override fun lex(fileName: String): Result {
        val reader = Reader(fileName)
        var actualWord = ""
        var tokensList = listOf<Token>()
        var startPos = Position()

        while (!reader.isEOF()) {
            val newChar = reader.next().toString()
            actualWord += newChar
            val nextChar = reader.peek()
            if (nextChar == '\n' || nextChar == '\r') {
                advancePosition(startPos, nextChar)
            }

            for (rule in rules) {
                if (rule.matches(actualWord, nextChar)) {
                    val posWithNewOffset = changingOffSet(startPos, actualWord)
                    val pos = posWithNewOffset.copy()
                    tokensList = rule.generateToken(tokensList, actualWord, pos)
                    val lastToken = tokensList.last()
                    val lastTokenIsUnknown = lastToken.getType() == TokenType.UNKNOWN
                    if (lastTokenIsUnknown){
                        return SyntaxError("Syntax error in line: " +
                                lastToken.getPosition().startLine)
                    }
                    startPos = advancePosition(posWithNewOffset, nextChar)
                    actualWord = resetWordToEmpty()
                    break
                }
            }
            var finalTokenList = tokensList.toMutableList()
            finalTokenList.add(Token(TokenType.EOF,"EOF", advancePosition(startPos, null)))

        }
        return Valid(tokensList)
    }

    private fun resetWordToEmpty(): String {
        return ""
    }

    private fun advancePosition(
        position: Position,
        char: Char?
    ): Position {
        return when (char) {
            '\n' -> position.copy(
                endOffset = -1,
                startLine = position.endLine + 1,
                startColumn = 0,
                endColumn = 0,
                endLine = position.endLine + 1
            )
            '\r' -> position.copy(
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
}