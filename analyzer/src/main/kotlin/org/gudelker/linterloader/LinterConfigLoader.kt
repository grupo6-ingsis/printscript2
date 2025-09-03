package org.gudelker.linterloader

import org.gudelker.LinterConfig

interface LinterConfigLoader {
    fun loadConfig(): Map<String, LinterConfig>
}
