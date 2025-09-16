package org.gudelker.interpreter

import org.gudelker.utilities.Version

object ChunkBaseFactory {
    fun createInterpreter(version: Version): ChunkBaseInterpreter {
        val evaluators = ListCase().listForVersion(version)
        return ChunkBaseInterpreter(evaluators)
    }
}
