package org.gudelker.interpreter

import org.gudelker.inputprovider.InputProvider
import org.gudelker.utilities.Version

object ChunkBaseFactory {
    fun createInterpreter(
        version: Version,
        provider: InputProvider,
    ): ChunkBaseInterpreter {
        val evaluators = ListCase().listForVersion(version, provider)
        return ChunkBaseInterpreter(evaluators)
    }
}
