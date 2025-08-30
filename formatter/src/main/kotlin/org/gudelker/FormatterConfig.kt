package org.gudelker
import kotlinx.serialization.Serializable

@Serializable
data class FormatterConfig(
    val spaceBeforeColon: Boolean,
    val spaceAfterColon: Boolean,
    val spaceBeforeEquals: Boolean,
    val spaceAfterEquals: Boolean,
    val newlinesBeforePrintln: Int,
)
