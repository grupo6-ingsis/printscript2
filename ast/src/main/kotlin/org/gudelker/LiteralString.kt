package org.gudelker

import org.gudelker.stmtposition.ComboValuePosition

data class LiteralString(
    val value: ComboValuePosition<String>,
) : ExpressionStatement
