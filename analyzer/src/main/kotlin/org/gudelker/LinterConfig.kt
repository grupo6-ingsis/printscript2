package org.gudelker
import kotlinx.serialization.Serializable

@Serializable
data class LinterConfig(
    val identifierFormat: String,
    val restrictPrintlnExpressions: Boolean,
)
