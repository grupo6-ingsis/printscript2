package org.gudelker.expressions

import org.gudelker.stmtposition.ComboValuePosition

data class LiteralBoolean(
    val value: ComboValuePosition<Boolean>,
) : BooleanExpressionStatement
