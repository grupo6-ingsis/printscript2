package org.gudelker

import org.gudelker.smtposition.ComboValuePosition

data class LiteralString(
    val value: ComboValuePosition<String>,
) : ExpressionStatement
