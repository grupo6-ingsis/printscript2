package org.example.interpreter

import org.gudelker.interpreter.DefaultInterpreter
import org.gudelker.interpreter.ListCase
import org.gudelker.utilities.Version

object SecondFactory {
    fun createInterpreter(version: Version): DefaultInterpreter {
        val evaluators = ListCase().listForVersion(version)
        return DefaultInterpreter(emptyList(), evaluators)
    }
}
