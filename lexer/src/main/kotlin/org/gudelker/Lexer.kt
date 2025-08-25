package org.gudelker

import org.gudelker.result.Result
import org.gudelker.sourcereader.SourceReader

interface Lexer {
    fun lex(sourceReader: SourceReader): Result
}
