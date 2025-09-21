package org.gudelker

import org.gudelker.evaluator.Evaluator
import org.gudelker.interpreter.StreamingInterpreter
import org.gudelker.interpreter.StreamingInterpreterResult
import org.gudelker.lexer.StreamingLexer
import org.gudelker.lexer.StreamingLexerResult
import org.gudelker.parser.StreamingParser
import org.gudelker.parser.StreamingParserResult
import org.gudelker.sourcereader.SourceReader

sealed class StreamingPipelineResult {
    data class StatementProcessed(val result: Any?) : StreamingPipelineResult()

    data class Error(val stage: String, val message: String) : StreamingPipelineResult()

    object Finished : StreamingPipelineResult()
}

class StreamingPipeline(
    private val streamingLexer: StreamingLexer,
    private val streamingParser: StreamingParser,
    private val streamingInterpreter: StreamingInterpreter,
) {
    private var isInitialized = false
    private var isFinished = false
    private val minBufferSize = 5
    private val maxBufferSize = 20 // Límite máximo para evitar problemas de memoria

    fun initialize(sourceReader: SourceReader) {
        streamingLexer.initialize(sourceReader)
        isInitialized = true
        isFinished = false
    }

    fun processNext(): StreamingPipelineResult {
        if (!isInitialized) {
            return StreamingPipelineResult.Error("Pipeline", "Not initialized")
        }

        if (isFinished) {
            return StreamingPipelineResult.Finished
        }

        while (streamingLexer.hasMore() || streamingParser.hasMore()) {
            // 1. Alimentar parser con tokens si es necesario y posible
            if (streamingLexer.hasMore()) {
                val tokensToRequest = 5
                when (val lexerResult = streamingLexer.nextBatch(tokensToRequest)) {
                    is StreamingLexerResult.TokenBatch -> {
                        streamingParser.addTokens(lexerResult.tokens)
                    }
                    is StreamingLexerResult.Error -> {
                        return StreamingPipelineResult.Error("Lexer", lexerResult.message)
                    }
                    StreamingLexerResult.Finished -> {
                        // El lexer terminó, pero podemos continuar con los tokens existentes
                    }
                }
            }

            // 2. Intentar parsear siguiente statement
            when (val parseResult = streamingParser.nextStatement()) {
                is StreamingParserResult.StatementParsed -> {
                    // 3. Interpretar el statement
                    when (val interpretResult = streamingInterpreter.processStatement(parseResult.statement)) {
                        is StreamingInterpreterResult.StatementEvaluated -> {
                            return StreamingPipelineResult.StatementProcessed(interpretResult.result)
                        }
                        is StreamingInterpreterResult.Error -> {
                            return StreamingPipelineResult.Error("Interpreter", interpretResult.message)
                        }
                        StreamingInterpreterResult.Finished -> {
                            isFinished = true
                            return StreamingPipelineResult.Finished
                        }
                    }
                }
                is StreamingParserResult.Error -> {
                    // Si necesita más tokens y el lexer puede proporcionar más, intentar de nuevo
                    if (parseResult.message.contains("Need more tokens", ignoreCase = true) &&
                        streamingLexer.hasMore()
                    ) {
                        continue // Intentar obtener más tokens en la siguiente iteración
                    }

                    // Si necesita más tokens pero el lexer terminó, es fin normal
                    if (parseResult.message.contains("Need more tokens", ignoreCase = true) &&
                        !streamingLexer.hasMore()
                    ) {
                        isFinished = true
                        return StreamingPipelineResult.Finished
                    }

                    return StreamingPipelineResult.Error("Parser", parseResult.message)
                }
                StreamingParserResult.Finished -> {
                    isFinished = true
                    return StreamingPipelineResult.Finished
                }
            }
        }

        // Si llegamos aquí, hemos intentado múltiples veces sin éxito
        return StreamingPipelineResult.Error(
            "Parser",
            "Could not parse statement after attempts. Parser buffer size: ${streamingParser.getBufferSize()}",
        )
    }

    fun processAll(onStatement: (Any?) -> Boolean = { true }): Boolean {
        while (!isFinished) {
            when (val result = processNext()) {
                is StreamingPipelineResult.StatementProcessed -> {
                    val shouldContinue = onStatement(result.result)
                    if (!shouldContinue) break
                }
                is StreamingPipelineResult.Error -> {
                    println("Pipeline error in ${result.stage}: ${result.message}")
                    return false
                }
                StreamingPipelineResult.Finished -> break
            }
        }
        return true
    }

    fun getFinalResults(): List<Any?> {
        return streamingInterpreter.getResults()
    }

    fun hasMore(): Boolean = !isFinished && (streamingLexer.hasMore() || streamingParser.hasMore())

    companion object {
        fun create(
            streamingLexer: StreamingLexer,
            streamingParser: StreamingParser,
            evaluators: List<Evaluator<out Any>>,
        ): StreamingPipeline {
            val streamingInterpreter = StreamingInterpreter(evaluators)
            return StreamingPipeline(streamingLexer, streamingParser, streamingInterpreter)
        }
    }
}
