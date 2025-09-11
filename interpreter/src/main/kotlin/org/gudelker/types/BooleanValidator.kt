package org.gudelker.types

class BooleanValidator : TypeValidator {
    override fun isInstance(value: Any): Boolean {
        return value is Boolean
    }
}
