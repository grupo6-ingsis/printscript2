package org.gudelker.result

class SyntaxError (val messageError: String) : Result {
    fun getError() : String {
        return messageError
    }
}