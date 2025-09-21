package org.gudelker

import org.gudelker.lexer.LexerFactory
import org.gudelker.lexer.StreamingLexer
import org.gudelker.lexer.StreamingLexerResult
import org.gudelker.resultlexer.LexerSyntaxError
import org.gudelker.sourcereader.StringSourceReader
import org.gudelker.token.TokenType
import org.gudelker.utilities.Version
import kotlin.test.Test

class LexerError {
    private val lexerV1 = LexerFactory.createLexer(Version.V1)
    private val lexerV2 = LexerFactory.createLexer(Version.V2)

    @Test
    fun `test lexer con símbolo no permitido`() {
        val invalidCode = "let x = @invalid;"
        val reader = StringSourceReader(invalidCode)
        val result = lexerV1.lex(reader)

        assert(result is LexerSyntaxError)
        if (result is LexerSyntaxError) {
            val errorMessage = result.getError()
            assert(errorMessage.contains("Error"))
        }
    }

    @Test
    fun `test lexer con string con comparador`() {
        val invalidCode = "let mensaje = =="
        val reader = StringSourceReader(invalidCode)
        val result = lexerV1.lex(reader)
        assert(result is LexerSyntaxError)
    }

    @Test
    fun `test lexer con operador de comparación incorrecto`() {
        val invalidCode = "if (x =< 5) {}"
        val reader = StringSourceReader(invalidCode)
        val result = lexerV1.lex(reader)

        assert(result is LexerSyntaxError)
    }

    @Test
    fun `test StreamingLexer con código válido`() {
        val validCode = "let x = 10; let y = 20;"
        val reader = StringSourceReader(validCode)
        val streamingLexer = StreamingLexer(lexerV2)

        streamingLexer.initialize(reader)
        val batch = streamingLexer.nextBatch()

        assert(batch is StreamingLexerResult.TokenBatch)
        if (batch is StreamingLexerResult.TokenBatch) {
            assert(batch.tokens.isNotEmpty())
            assert(batch.tokens.any { it.getType() == TokenType.KEYWORD })
        }
    }

    @Test
    fun `test StreamingLexer con error`() {
        val invalidCode = "let @ = 10;"
        val reader = StringSourceReader(invalidCode)
        val streamingLexer = StreamingLexer(lexerV1)

        streamingLexer.initialize(reader)
        val batch = streamingLexer.nextBatch()

        assert(batch is StreamingLexerResult.TokenBatch)
        if (batch is StreamingLexerResult.TokenBatch) {
            assert(batch.tokens.isNotEmpty())
            assert(batch.tokens.size < 3)
        }
    }
}
