package org.gudelker.types

class NumberValidator : TypeValidator {
    override fun isInstance(value: Any): Boolean {
        return value is Number
    }
}
