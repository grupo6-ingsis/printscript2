package org.gudelker.expressions

import org.gudelker.stmtposition.ComboValuePosition

data class LiteralString(
    val value: ComboValuePosition<String>,
) : ExpressionStatement
