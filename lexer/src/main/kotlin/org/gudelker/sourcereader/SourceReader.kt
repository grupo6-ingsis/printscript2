package org.gudelker.sourcereader

interface SourceReader {
    fun peek(): Char?
    fun next(): Char?
    fun isEOF(): Boolean
    fun close()
}