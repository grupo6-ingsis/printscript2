package org.gudelker.result

data class LintViolation(
    val message: String,
) : LinterResult {
    override fun toString(): String {
        return message
    }
}
