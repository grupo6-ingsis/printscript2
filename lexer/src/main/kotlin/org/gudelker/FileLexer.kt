package org.gudelker

class FileLexer(val rules : List<RuleTokenizer>) : Lexer {

    override fun lex(fileName: String): List<Token> {
        val reader = Reader(fileName)
        var actualWord = ""
        var tokensList = listOf<Token>()
        var startPos = Position()

        while (!reader.isEOF()) {
            val newChar = reader.next().toString()
            actualWord += newChar
            val nextChar = reader.peek()
            if (nextChar == '\n'){
                advancePosition(startPos, nextChar)
            }

            for (rule in rules) {
                if (rule.matches(actualWord, nextChar)) {
                    val posWithNewOffset = changingOffSet(startPos, actualWord)
                    val pos = posWithNewOffset.copy()
                    tokensList = rule.generateToken(tokensList, actualWord, pos)
                    startPos = advancePosition(posWithNewOffset, nextChar)
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

    private fun advancePosition( // No está bien implementado pero va por ahí
        position: Position,
        char: Char?
    ): Position {
        return when (char) {
            '\n' -> position.copy(
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