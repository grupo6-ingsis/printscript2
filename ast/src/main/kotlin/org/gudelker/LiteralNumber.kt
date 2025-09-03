package org.gudelker

import org.gudelker.smtposition.ComboValuePosition

data class LiteralNumber(
    val value: ComboValuePosition<Number>,
) : ExpressionStatement
