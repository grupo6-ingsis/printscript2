package org.gudelker.result

data class CompoundResult(val results: List<LintViolation>, val message: String) {
    override fun toString(): String {
        return message + results.joinToString("\n")
    }
}
