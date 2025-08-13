package org.gudelker

import java.io.FileReader

class Reader (fileName: String) {
    private val peeker = FileReader(fileName)
    private var nextChar: Int? = null

    fun peek(): Char? {
        if (nextChar == null) {
            nextChar = peeker.read()
        }
        return if (nextChar == -1) null else nextChar!!.toChar()
    }

    fun next(): Char? {
        val result = peek()
        nextChar = null
        return result
    }

    fun isEOF(): Boolean = peek() == null

    fun close() = peeker.close()
}