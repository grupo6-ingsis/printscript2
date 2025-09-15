package org.gudelker.linterloader

import org.gudelker.linter.LinterConfig

interface LinterConfigLoader {
    fun loadConfig(): Map<String, LinterConfig>
}
