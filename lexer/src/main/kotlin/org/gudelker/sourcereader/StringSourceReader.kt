package org.gudelker.sourcereader

class StringSourceReader(private val content: String) : SourceReader {
    private var position = 0

    override fun peek(): Char? {
        return if (position < content.length) {
            content[position]
        } else {
            null
        }
    }

    override fun next(): Char? {
        val result = peek()
        if (result != null) {
            position++
        }
        return result
    }

    override fun isEOF(): Boolean = position >= content.length

    override fun close() {
        // No hay nada que cerrar para strings
    }
}
