package org.gudelker.validators

import org.gudelker.LiteralIdentifier
import org.gudelker.LiteralNumber
import org.gudelker.LiteralString
import org.gudelker.Statement

class IsNotALiteral : StatementValidator {
    override fun validate(statement: Statement): Boolean {
        return statement !is LiteralIdentifier && statement !is LiteralString && statement !is LiteralNumber
    }
}
