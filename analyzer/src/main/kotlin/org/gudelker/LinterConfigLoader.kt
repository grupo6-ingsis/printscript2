package org.gudelker

interface LinterConfigLoader {
    fun loadConfig(): Map<String, LinterConfig>
}
