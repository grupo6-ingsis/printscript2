package org.example.interpreter

import org.gudelker.interpreter.ListCase
import org.gudelker.utilities.Version

object SecondFactory {
    fun createTheInterpreter(version: Version): SecondInterpreter {
        val evaluators = ListCase().listForVersion(version)
        return SecondInterpreter(emptyList(), evaluators)
    }
}
