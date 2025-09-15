package org.gudelker.lexer

import org.gudelker.result.LexerResult
import org.gudelker.sourcereader.SourceReader

interface Lexer {
    fun lex(sourceReader: SourceReader): LexerResult
}
