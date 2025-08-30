package org.gudelker

interface FormatterConfigLoader {
    val path: String

    fun loadConfig(): FormatterConfig
}
