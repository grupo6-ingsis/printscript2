package org.gudelker.expressions

import org.gudelker.stmtposition.ComboValuePosition

data class LiteralNumber(
    val value: ComboValuePosition<Number>,
) : ExpressionStatement
