package org.gudelker.inputprovider

class ReadProvider(private val value: String) : InputProvider {
    override fun nextInput(prompt: String): String {
        print(prompt)
        return value
    }
}
