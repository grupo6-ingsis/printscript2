package org.gudelker.smtposition

data class ComboValuePosition<T>(val value: T, val position: StatementPosition) {
    override fun toString(): String {
        return "$value"
    }
}
