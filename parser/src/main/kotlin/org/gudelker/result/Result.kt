package org.gudelker.result

interface Result {
    fun isValid(): Boolean = this is Valid
}
