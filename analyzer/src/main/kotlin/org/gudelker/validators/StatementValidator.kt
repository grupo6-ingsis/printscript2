package org.gudelker.validators

import org.gudelker.Statement

interface StatementValidator {
    fun validate(statement: Statement): Boolean
}
