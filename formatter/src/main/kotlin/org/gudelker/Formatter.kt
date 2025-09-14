package org.gudelker

import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.rules.FormatterRule

interface Formatter {
    fun format(
        tokenStream: TokenStream,
        rules: Map<String, FormatterRule>,
    ): String
}
