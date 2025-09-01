package org.gudelker.string

class CamelCaseValidator : StringValidator {
    override fun validateString(value: String): Boolean {
        val camelCaseRegex = "^[a-z]+(?:[A-Z][a-z]*)*$".toRegex()
        return camelCaseRegex.matches(value)
    }
}
