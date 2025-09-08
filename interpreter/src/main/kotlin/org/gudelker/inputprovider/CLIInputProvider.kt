package org.gudelker.inputprovider

class CLIInputProvider : InputProvider {
    override fun nextInput(prompt: String): String {
        print(prompt)
        return readln()
    }
}
