package org.gudelker

import org.gudelker.rules.AssignationTokenizer
import org.gudelker.rules.BooleanTokenizer
import org.gudelker.rules.BooleanTypeTokenizer
import org.gudelker.rules.BracketTokenizer
import org.gudelker.rules.ColonTokenizer
import org.gudelker.rules.ComparatorTokenizer
import org.gudelker.rules.ConstTokenizer
import org.gudelker.rules.DoubleTokenizer
import org.gudelker.rules.ElseTokenizer
import org.gudelker.rules.EqualComparativeTokenizer
import org.gudelker.rules.IdentifierTokenizer
import org.gudelker.rules.IfTokenizer
import org.gudelker.rules.IntegerTokenizer
import org.gudelker.rules.LetTokenizer
import org.gudelker.rules.NewLineTokenizer
import org.gudelker.rules.NotLineAfterSemicolonTokenizer
import org.gudelker.rules.NotSpaceOperationTokenizer
import org.gudelker.rules.NumberTypeTokenizer
import org.gudelker.rules.OperationTokenizer
import org.gudelker.rules.ParenthesisTokenizer
import org.gudelker.rules.PrintTokenizer
import org.gudelker.rules.ProhibitedSymbolDoubleTokenizer
import org.gudelker.rules.SemicolonTokenizer
import org.gudelker.rules.StringTokenizer
import org.gudelker.rules.StringTypeTokenizer
import org.gudelker.rules.WhitespaceTokenizer
import org.gudelker.utilities.Version

object LexerFactory {
    fun createLexer(version: Version): DefaultLexer {
        val listOfRules =
            listOf(
                LetTokenizer(),
                PrintTokenizer(),
                ColonTokenizer(),
                NewLineTokenizer(),
                NotLineAfterSemicolonTokenizer(),
                SemicolonTokenizer(),
                OperationTokenizer(),
                StringTokenizer(),
                WhitespaceTokenizer(),
                AssignationTokenizer(),
                NotSpaceOperationTokenizer(),
                ProhibitedSymbolDoubleTokenizer(),
                StringTypeTokenizer(),
                NumberTypeTokenizer(),
                IdentifierTokenizer(),
                DoubleTokenizer(),
                IntegerTokenizer(),
                ParenthesisTokenizer(),
            )
        if (version == Version.V1) {
            return DefaultLexer(listOfRules)
        } else if (version == Version.V2) {
            val newTokenizers: List<RuleTokenizer> =
                listOf(
                    ConstTokenizer(),
                    EqualComparativeTokenizer(),
                    BracketTokenizer(),
                    BooleanTypeTokenizer(),
                    IfTokenizer(),
                    ComparatorTokenizer(),
                    ElseTokenizer(),
                    BooleanTokenizer(),
                )
            val listV2: List<RuleTokenizer> = newTokenizers + listOfRules
            return DefaultLexer(listV2)
        } else {
            throw IllegalArgumentException("Versión no soportada: $version")
        }
    }
}
