package org.gudelker.expressions

import org.gudelker.stmtposition.ComboValuePosition

data class LiteralIdentifier(
    val value: ComboValuePosition<String>,
) : BooleanExpressionStatement
