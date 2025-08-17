package org.gudelker.result

interface Result {
    fun isValid() : Boolean{
        return this is Valid
    }
}