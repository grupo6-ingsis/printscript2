package org.gudelker.rule

import org.gudelker.Token
import org.gudelker.result.Result

interface SyntaxRule {
    fun matches(tokens: List<Token>, index: Int): Boolean
    fun parse(tokens: List<Token>, index: Int): Result
}