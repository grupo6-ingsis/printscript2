package org.gudelker

import org.gudelker.rules.AssignationTokenizer
import org.gudelker.rules.ColonTokenizer
import org.gudelker.rules.DoubleTokenizer
import org.gudelker.rules.EqualComparativeTokenizer
import org.gudelker.rules.IdentifierTokenizer
import org.gudelker.rules.IntegerTokenizer
import org.gudelker.rules.LetTokenizer
import org.gudelker.rules.NewLineTokenizer
import org.gudelker.rules.NotLineAfterSemicolonTokenizer
import org.gudelker.rules.ProhibitedSymbolDoubleTokenizer
import org.gudelker.rules.SemicolonTokenizer
import org.gudelker.rules.WhitespaceTokenizer

object LexerFactory {
    fun createFileLexer() : FileLexer{
        return FileLexer(
            listOf(
                LetTokenizer(),
                ColonTokenizer(),
                NotLineAfterSemicolonTokenizer(),
                SemicolonTokenizer(),
                WhitespaceTokenizer(),
                NewLineTokenizer(),
                EqualComparativeTokenizer(),
                AssignationTokenizer(),
                ProhibitedSymbolDoubleTokenizer(),
                IdentifierTokenizer(),
                DoubleTokenizer(),
                IntegerTokenizer(),
            )
        )
    }
}