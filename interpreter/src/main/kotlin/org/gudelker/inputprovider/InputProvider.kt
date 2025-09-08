package org.gudelker.inputprovider

interface InputProvider {
    fun nextInput(prompt: String): String
}
