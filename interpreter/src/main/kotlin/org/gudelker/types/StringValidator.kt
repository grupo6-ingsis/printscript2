package org.gudelker.types

class StringValidator : TypeValidator {
    override fun isInstance(value: Any): Boolean {
        return value is String
    }
}
