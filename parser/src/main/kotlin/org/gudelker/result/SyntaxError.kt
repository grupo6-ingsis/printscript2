package org.gudelker.result


class SyntaxError(private val errorMessage : String) : Result {

    fun getError() : String {
        return errorMessage
    }
}