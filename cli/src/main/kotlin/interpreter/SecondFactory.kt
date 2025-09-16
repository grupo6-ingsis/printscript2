package org.example.interpreter

import org.gudelker.inputprovider.InputProvider
import org.gudelker.interpreter.ListCase
import org.gudelker.utilities.Version

object SecondFactory {
    fun createTheInterpreter(
        version: Version,
        provider: InputProvider,
    ): SecondInterpreter {
        val evaluators = ListCase().listForVersion(version, provider)
        return SecondInterpreter(emptyList(), evaluators)
    }
}
