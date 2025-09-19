package org.gudelker.lexer

import org.gudelker.resultlexer.LexerResult
import org.gudelker.sourcereader.SourceReader

interface Lexer {
    fun lex(sourceReader: SourceReader): LexerResult
}
