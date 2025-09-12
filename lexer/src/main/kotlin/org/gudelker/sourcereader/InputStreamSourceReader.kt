package org.gudelker.sourcereader

import java.io.InputStream

class InputStreamSourceReader(
    private val input: InputStream,
    private val bufferSize: Int = 8192,
) : SourceReader {
    private val buffer = ByteArray(bufferSize)
    private var bufferPos = 0
    private var bufferEnd = 0
    private var eof = false
    private var nextChar: Char? = null

    override fun peek(): Char? {
        if (nextChar != null) return nextChar
        nextChar = readNextChar()
        return nextChar
    }

    override fun next(): Char? {
        val current = peek()
        nextChar = null
        return current
    }

    override fun isEOF(): Boolean {
        return eof && peek() == null
    }

    override fun close() {
        input.close()
    }

    private fun readNextChar(): Char? {
        if (bufferPos >= bufferEnd) {
            // cargar más bytes al buffer
            bufferEnd = input.read(buffer)
            bufferPos = 0
            if (bufferEnd == -1) {
                eof = true
                return null
            }
        }
        val b = buffer[bufferPos++].toInt() and 0xFF
        return b.toChar()
    }
}
