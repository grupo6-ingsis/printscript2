package org.gudelker

import org.gudelker.stmtposition.ComboValuePosition

data class LiteralNumber(
    val value: ComboValuePosition<Number>,
) : ExpressionStatement
