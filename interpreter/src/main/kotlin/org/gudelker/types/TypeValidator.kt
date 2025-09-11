package org.gudelker.types

interface TypeValidator {
    fun isInstance(value: Any): Boolean
}
