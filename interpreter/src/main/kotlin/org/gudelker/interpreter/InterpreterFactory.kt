package org.gudelker.interpreter

import org.gudelker.utilities.Version

object InterpreterFactory {
    fun createInterpreter(version: Version): DefaultInterpreter {
        val evaluators = ListCase().listForVersion(version)
        return DefaultInterpreter(emptyList(), evaluators)
    }
}
