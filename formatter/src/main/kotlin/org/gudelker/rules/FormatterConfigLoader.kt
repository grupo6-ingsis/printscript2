package org.gudelker.rules

interface FormatterConfigLoader {
    fun loadConfig(): Map<String, FormatterRule>
}
