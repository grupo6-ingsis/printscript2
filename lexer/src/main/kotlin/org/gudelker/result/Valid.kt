package org.gudelker.result

import org.gudelker.Token

class Valid (val value : List<Token>) : Result {
    fun getList() : List<Token> {
        return value
    }
}
