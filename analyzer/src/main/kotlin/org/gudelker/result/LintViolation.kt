package org.gudelker.result

import org.gudelker.stmtposition.StatementPosition

data class LintViolation(
    val message: String,
    val position: StatementPosition,
) : LinterResult {
    override fun toString(): String {
        return message
    }
}
