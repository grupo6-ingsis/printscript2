package org.gudelker.linter

import kotlinx.serialization.Serializable

@Serializable
data class LinterConfig(
    val identifierFormat: String,
    val restrictPrintlnExpressions: Boolean,
    val restrictReadInputExpressions: Boolean,
)
