package org.gudelker.interpreter

import org.gudelker.inputprovider.InputProvider
import org.gudelker.utilities.Version

object InterpreterFactory {
    fun createInterpreter(
        version: Version,
        provider: InputProvider,
    ): DefaultInterpreter {
        val evaluators = ListCase().listForVersion(version, provider)
        return DefaultInterpreter(emptyList(), evaluators)
    }
}
