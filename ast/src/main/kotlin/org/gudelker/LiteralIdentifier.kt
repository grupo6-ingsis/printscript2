package org.gudelker

import org.gudelker.smtposition.ComboValuePosition

data class LiteralIdentifier(
    val value: ComboValuePosition<String>,
) : ExpressionStatement
