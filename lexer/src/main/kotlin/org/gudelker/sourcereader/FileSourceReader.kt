package org.gudelker.sourcereader

import java.io.FileReader
import java.io.BufferedReader

class FileSourceReader(private val filename: String) : SourceReader {
    private val reader = BufferedReader(FileReader(filename))
    private var nextChar: Int? = null

    override fun peek(): Char? {
        if (nextChar == null) {
            nextChar = reader.read()
        }
        return if (nextChar == -1) null else nextChar!!.toChar()
    }

    override fun next(): Char? {
        val result = peek()
        nextChar = null
        return result
    }

    override fun isEOF(): Boolean = peek() == null

    override fun close() = reader.close()
}