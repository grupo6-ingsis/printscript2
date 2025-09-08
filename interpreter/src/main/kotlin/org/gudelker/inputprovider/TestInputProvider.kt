package org.gudelker.inputprovider

class TestInputProvider(private val inputs: MutableList<String>) : InputProvider {
    override fun nextInput(prompt: String): String {
        if (inputs.isEmpty()) throw IllegalStateException("No hay más valores de entrada")
        return inputs.removeAt(0)
    }
}
