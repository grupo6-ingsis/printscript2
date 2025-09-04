package org.gudelker

import org.gudelker.stmtposition.ComboValuePosition

data class LiteralIdentifier(
    val value: ComboValuePosition<String>,
) : ExpressionStatement
