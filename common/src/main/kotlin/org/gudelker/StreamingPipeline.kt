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

    data object Finished : StreamingPipelineResult()
}

class StreamingPipeline(
    private val streamingLexer: StreamingLexer,
    private val streamingParser: StreamingParser,
    private val streamingInterpreter: StreamingInterpreter,
) {
    private var isInitialized = false
    private var isFinished = false
    private var lastErrorMessage: String? = null

    fun getLastErrorMessage(): String? = lastErrorMessage

    fun initialize(sourceReader: SourceReader) {
        streamingLexer.initialize(sourceReader)
        isInitialized = true
        isFinished = false
    }

    fun processNext(): StreamingPipelineResult {
        if (!isInitialized) {
            lastErrorMessage = "Not initialized"
            return StreamingPipelineResult.Error("Pipeline", "Not initialized")
        }

        if (isFinished) {
            return StreamingPipelineResult.Finished
        }

        while (streamingLexer.hasMore() || streamingParser.hasMore()) {
            if (streamingLexer.hasMore()) {
                val tokensToRequest = 5
                when (val lexerResult = streamingLexer.nextBatch(tokensToRequest)) {
                    is StreamingLexerResult.TokenBatch -> {
                        streamingParser.addTokens(lexerResult.tokens)
                    }
                    is StreamingLexerResult.Error -> {
                        lastErrorMessage = lexerResult.message
                        return StreamingPipelineResult.Error("Lexer", lexerResult.message)
                    }
                    StreamingLexerResult.Finished -> {
                        // El lexer terminó, pero podemos continuar con los tokens existentes
                    }
                }
            }

            when (val parseResult = streamingParser.nextStatement()) {
                is StreamingParserResult.StatementParsed -> {
                    when (val interpretResult = streamingInterpreter.processStatement(parseResult.statement)) {
                        is StreamingInterpreterResult.StatementEvaluated -> {
                            return StreamingPipelineResult.StatementProcessed(interpretResult.result)
                        }
                        is StreamingInterpreterResult.Error -> {
                            lastErrorMessage = interpretResult.message
                            return StreamingPipelineResult.Error("Interpreter", interpretResult.message)
                        }
                        StreamingInterpreterResult.Finished -> {
                            isFinished = true
                            return StreamingPipelineResult.Finished
                        }
                    }
                }
                is StreamingParserResult.Error -> {
                    lastErrorMessage = parseResult.message
                    if (parseResult.message.contains("Need more tokens", ignoreCase = true) &&
                        streamingLexer.hasMore()
                    ) {
                        continue
                    }

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

        lastErrorMessage = "Could not parse statement after attempts. Parser buffer size: ${streamingParser.getBufferSize()}"
        return StreamingPipelineResult.Error(
            "Parser",
            lastErrorMessage!!,
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
