package org.gudelker

import org.gudelker.formatter.DefaultFormatterFactory
import org.gudelker.inputprovider.CLIInputProvider
import org.gudelker.interpreter.ChunkBaseFactory
import org.gudelker.lexer.LexerFactory
import org.gudelker.lexer.StreamingLexer
import org.gudelker.lexer.StreamingLexerResult
import org.gudelker.linter.DefaultLinterFactory
import org.gudelker.linter.LinterConfig
import org.gudelker.parser.DefaultParserFactory
import org.gudelker.parser.StreamingParser
import org.gudelker.parser.StreamingParserResult
import org.gudelker.result.CompoundResult
import org.gudelker.rules.FormatterRule
import org.gudelker.sourcereader.StringSourceReader
import org.gudelker.statements.interfaces.Statement
import org.gudelker.utilities.Version
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StreamingPipelineTest {
    @Test
    fun `should process simple variable declaration and usage with streaming pipeline`() {
        val code =
            """
            let x:number;
            x = 42.2;
            println(x);
            """.trimIndent()

        val results = processCodeWithStreamingPipeline(Version.V2, code)

        assertEquals(3, results.size)
        assertEquals("42.2", results[2])
    }

    @Test
    fun `should process if else with streaming pipeline`() {
        val code =
            """
            if(false) {
                let y:number = 2;
                let x:number = 2;
            }else {
                println("else statement working correctly");
            }
            println("outside of conditional");
            """.trimIndent()

        val results = processCodeWithStreamingPipeline(Version.V2, code)

        assertEquals(2, results.size)
    }

    @Test
    fun `should process mathematical operations with streaming pipeline`() {
        val code =
            """
            let x = 5+3;
            let y = x * 2;
            println(y - 1);
            """.trimIndent()

        val results = processCodeWithStreamingPipeline(Version.V2, code)

        assertEquals(3, results.size)
        assertEquals("15", results[2])
    }

    @Test
    fun `should process complex expressions incrementally`() {
        val code =
            """
            let a = 10;
            let b = 5;
            let c = (a + b);
            let result = c / 3;
            println(result);
            """.trimIndent()

        val results = mutableListOf<Any?>()
        val pipeline = createStreamingPipeline()
        pipeline.initialize(StringSourceReader(code))

        // Procesar statement por statement
        var statementCount = 0
        while (pipeline.hasMore()) {
            when (val result = pipeline.processNext()) {
                is StreamingPipelineResult.StatementProcessed -> {
                    results.add(result.result)
                    statementCount++
                    println("Statement $statementCount processed: ${result.result}")
                }
                is StreamingPipelineResult.Error -> {
                    throw RuntimeException("Pipeline error in ${result.stage}: ${result.message}")
                }
                StreamingPipelineResult.Finished -> break
            }
        }

        assertEquals(5, results.size)
        assertEquals(5, statementCount)
    }

    @Test
    fun `should process string operations with streaming pipeline`() {
        val code =
            """
            let greeting = "Hello";
            let name = "World";
            println(greeting + " " + name);
            """.trimIndent()

        val results = processCodeWithStreamingPipeline(Version.V2, code)

        assertEquals(3, results.size)
    }

    @Test
    fun `should handle if-else statements with streaming pipeline`() {
        val code =
            """
            if (true) {
                println("Hello World!");
                println("Greater than 5");
            }
            """.trimIndent()

        val results = processCodeWithStreamingPipeline(Version.V2, code)

        assertEquals(1, results.size)
    }

    @Test
    fun `should process mixed operations with callback processing`() {
        val code =
            """
            let x = 5;
            let y = - x + 10;
            let z = y * (x - 2);
            println(z);
            println(z / 5);
            """.trimIndent()

        val processedResults = mutableListOf<String>()
        val pipeline = createStreamingPipeline()
        pipeline.initialize(StringSourceReader(code))

        // Usar processAll con callback personalizado
        val success =
            pipeline.processAll { result ->
                processedResults.add(result.toString())
                true // continuar procesando
            }

        assert(success)
        assertEquals(5, processedResults.size)
        assertEquals("15", processedResults[3]) // println(z)
        assertEquals("3", processedResults[4]) // println(z / 5)
    }

    @Test
    fun `should handle early termination with callback`() {
        val code =
            """
            let x = 1;
            let y = 2;
            println("First");
            let z = 3;
            println("Second");
            let w = 4;
            println("Third");
            """.trimIndent()

        val processedResults = mutableListOf<Any?>()
        val pipeline = createStreamingPipeline()
        pipeline.initialize(StringSourceReader(code))

        // Procesar solo hasta el tercer statement
        val success =
            pipeline.processAll { result ->
                processedResults.add(result)
                processedResults.size < 3 // parar después de 3 statements
            }

        assert(success)
        assertEquals(3, processedResults.size)

        // Verificar que el pipeline puede continuar después
        val remainingResults = mutableListOf<Any?>()
        pipeline.processAll { result ->
            remainingResults.add(result)
            true
        }

        assertEquals(4, remainingResults.size) // Los 4 statements restantes
    }

    @Test
    fun `should demonstrate step-by-step processing with detailed output`() {
        val code =
            """
            let total = 0;
            let count = 5;
            total = total + count;
            println("Total: " + total);
            """.trimIndent()

        val pipeline = createStreamingPipeline()
        pipeline.initialize(StringSourceReader(code))

        var stepNumber = 1
        while (pipeline.hasMore()) {
            println("\n--- Step $stepNumber ---")

            when (val result = pipeline.processNext()) {
                is StreamingPipelineResult.StatementProcessed -> {
                    println("✓ Statement executed successfully")
                    println("  Result: ${result.result}")
                    stepNumber++
                }
                is StreamingPipelineResult.Error -> {
                    println("✗ Error in ${result.stage}: ${result.message}")
                    throw RuntimeException("Pipeline failed at step $stepNumber")
                }
                StreamingPipelineResult.Finished -> {
                    println("✓ Pipeline finished successfully")
                    break
                }
            }
        }

        val finalResults = pipeline.getFinalResults()
        assertEquals(4, finalResults.size)
        println("\nFinal results: $finalResults")
    }

    private fun processCodeWithStreamingPipeline(
        version: Version = Version.V1,
        code: String,
    ): List<Any?> {
        val pipeline = createStreamingPipeline(version)
        pipeline.initialize(StringSourceReader(code))

        val success = pipeline.processAll()
        if (!success) {
            throw RuntimeException("Streaming pipeline failed")
        }

        return pipeline.getFinalResults()
    }

    private fun createStreamingPipeline(version: Version = Version.V1): StreamingPipeline {
        // 1. Crear componentes base
        val defaultLexer = LexerFactory.createLexer(version)
        val defaultParser = DefaultParserFactory.createParser(version)

        // 2. Crear wrappers streaming
        val streamingLexer = StreamingLexer(defaultLexer)
        val streamingParser = StreamingParser(defaultParser)

        // 3. Crear evaluadores para el interpreter
        val chunkBaseInterpreter = ChunkBaseFactory.createInterpreter(version, CLIInputProvider())
        val evaluators = chunkBaseInterpreter.getEvaluators()

        // 4. Crear pipeline
        return StreamingPipeline.create(streamingLexer, streamingParser, evaluators)
    }

    @Test
    fun `should lint code with camelCase and snake_case identifiers`() {
        val code =
            """
            const myConst = true;
            let another_var = 42;
            println(myConst);
            """.trimIndent()

        val result = lintCodeV2WithPipeline(code)
        assert(result.results.any { true })
        assertEquals(1, result.results.count { true })
    }

    @Test
    fun `should lint code with multiple violations`() {
        val code =
            """
            const bad_var = false;
            let another_var = 7;
            println(bad_var);
            println(another_var);
            """.trimIndent()

        val result = lintCodeV2WithPipeline(code)
        assert(result.results.any { true })
        assertEquals(2, result.results.count { true })
    }

    @Test
    fun `should lint code with restrictReadInputExpressions`() {
        val code =
            """
            const myConst = true;
            let another_var = 42;
            println(myConst);
            """.trimIndent()

        val result = lintCodeV2WithPipeline(code)
        assert(result.results.any { true })
        assertEquals(1, result.results.count { true })
    }

    @Test
    fun `should lint println allowing only literals in V2`() {
        val code =
            """
            println("hello");
            println(42);
            println(true);
            println(1 + 2);
            let x = 5;
            println(x);
            """.trimIndent()

        val result = lintCodeV2WithPipeline(code)
        assert(result.results.any { true })
        assertEquals(1, result.results.count { true })
    }

    private fun lintCodeV2WithPipeline(code: String): CompoundResult {
        val defaultLexer = LexerFactory.createLexer(Version.V2)
        val defaultParser = DefaultParserFactory.createParser(Version.V2)
        val streamingLexer = StreamingLexer(defaultLexer)
        val streamingParser = StreamingParser(defaultParser)
        val sourceReader = StringSourceReader(code)

        streamingLexer.initialize(sourceReader)

        val statements = mutableListOf<org.gudelker.statements.interfaces.Statement>()
        while (streamingLexer.hasMore() || streamingParser.hasMore()) {
            if (streamingLexer.hasMore()) {
                val lexerResult = streamingLexer.nextBatch(10)
                if (lexerResult is StreamingLexerResult.TokenBatch) {
                    streamingParser.addTokens(lexerResult.tokens)
                }
            }
            val parseResult = streamingParser.nextStatement()
            when (parseResult) {
                is StreamingParserResult.StatementParsed -> statements.add(parseResult.statement)
                is StreamingParserResult.Error -> {
                    if (parseResult.message.contains("Need more tokens", ignoreCase = true)) continue
                    break
                }
                StreamingParserResult.Finished -> break
            }
        }

        val linter = DefaultLinterFactory.createLinter(Version.V2)
        val rules =
            mapOf(
                "identifierFormat" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
                "restrictPrintlnExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
                "restrictReadInputExpressions" to
                    LinterConfig(
                        identifierFormat = "camelCase", restrictPrintlnExpressions = true,
                        restrictReadInputExpressions = true,
                    ),
            )
        return linter.lint(org.gudelker.stmtposition.StatementStream(statements), rules)
    }

    @Test
    fun `should return formatted code applying rules with pipeline`() {
        val code =
            """
              let something:string = "a really cool thing";
                let another_thing: string = "another really cool thing";
            if (true) {
              let x = 5;
              let y = 10;
            }
            """.trimIndent()

        val result = formatCodeV2WithPipeline(code)

        val expectedCode =
            """
            let something:string = "a really cool thing";
            let another_thing: string = "another really cool thing";
            if (true) {
            let x = 5;
            let y = 10;
            }
            """.trimIndent()
        println(result.replace("\n", "\\n\n"))
        assertEquals(expectedCode, result)
    }

    private fun formatCodeV2WithPipeline(code: String): String {
        val defaultLexer = LexerFactory.createLexer(Version.V2)
        val defaultParser = DefaultParserFactory.createParser(Version.V2)
        val streamingLexer = StreamingLexer(defaultLexer)
        val streamingParser = StreamingParser(defaultParser)
        val sourceReader = StringSourceReader(code)

        streamingLexer.initialize(sourceReader)

        val statements = mutableListOf<Statement>()
        while (streamingLexer.hasMore() || streamingParser.hasMore()) {
            if (streamingLexer.hasMore()) {
                val lexerResult = streamingLexer.nextBatch(10)
                if (lexerResult is StreamingLexerResult.TokenBatch) {
                    streamingParser.addTokens(lexerResult.tokens)
                }
            }
            val parseResult = streamingParser.nextStatement()
            when (parseResult) {
                is StreamingParserResult.StatementParsed -> statements.add(parseResult.statement)
                is StreamingParserResult.Error -> {
                    if (parseResult.message.contains("Need more tokens", ignoreCase = true)) continue
                    break
                }
                StreamingParserResult.Finished -> break
            }
        }
        val rules =
            mapOf(
                "enforce-spacing-before-colon-in-declaration" to FormatterRule(on = false, quantity = 1),
                "enforce-spacing-after-colon-in-declaration" to FormatterRule(on = false, quantity = 2),
                "enforce-spacing-around-equals" to FormatterRule(on = false, quantity = 1),
                "line-breaks-after-println" to FormatterRule(on = true, quantity = 1),
                "mandatory-line-break-after-statement" to FormatterRule(on = true, quantity = 1),
                "indent-inside-if" to FormatterRule(on = false, quantity = 4),
            )

        val formatter = DefaultFormatterFactory.createFormatter(Version.V2)
        val builder = StringBuilder()
        for (statement in statements) {
            builder.append(formatter.format(statement, rules))
        }
        return builder.toString()
    }

    @Test
    fun `should process readInput and println statements with TestInputProvider`() {
        val code =
            """
            let boca:String = readInput("hola");
            println(boca);
            """.trimIndent()

        val inputProvider = org.gudelker.inputprovider.TestInputProvider(mutableListOf("test input"))
        val pipeline =
            createStreamingPipeline(
                version = org.gudelker.utilities.Version.V2,
                inputProvider = inputProvider,
            )
        pipeline.initialize(StringSourceReader(code))

        val results = mutableListOf<Any?>()
        pipeline.processAll { result ->
            results.add(result)
            true
        }

        // The first result is the value assigned to boca, the second is println(boca)
        assertEquals(2, results.size)
        assertEquals("test input", results[1])
    }

    // Helper to inject custom InputProvider
    private fun createStreamingPipeline(
        version: org.gudelker.utilities.Version = org.gudelker.utilities.Version.V1,
        inputProvider: org.gudelker.inputprovider.InputProvider,
    ): StreamingPipeline {
        val defaultLexer = LexerFactory.createLexer(version)
        val defaultParser = DefaultParserFactory.createParser(version)
        val streamingLexer = StreamingLexer(defaultLexer)
        val streamingParser = StreamingParser(defaultParser)
        val chunkBaseInterpreter = ChunkBaseFactory.createInterpreter(version, inputProvider)
        val evaluators = chunkBaseInterpreter.getEvaluators()
        return StreamingPipeline.create(streamingLexer, streamingParser, evaluators)
    }
}
