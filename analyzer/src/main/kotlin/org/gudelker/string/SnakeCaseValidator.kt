package org.gudelker.string

class SnakeCaseValidator : StringValidator {
    override fun validateString(value: String): Boolean {
        val snakeCaseRegex = Regex("^[a-z]+(_[a-z]+)*$")
        return snakeCaseRegex.matches(value)
    }
}
