package org.gudelker.cli
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import org.gudelker.StreamingPipeline
import org.gudelker.formatter.DefaultFormatterFactory
import org.gudelker.inputprovider.CLIInputProvider
import org.gudelker.interpreter.ChunkBaseFactory
import org.gudelker.interpreter.StreamingInterpreter
import org.gudelker.lexer.LexerFactory
import org.gudelker.lexer.StreamingLexer
import org.gudelker.lexer.StreamingLexerResult
import org.gudelker.linter.DefaultLinterFactory
import org.gudelker.linterloader.JsonLinterConfigLoaderToMap
import org.gudelker.linterloader.LinterConfigLoader
import org.gudelker.linterloader.YamlLinterConfigLoaderToMap
import org.gudelker.parser.DefaultParserFactory
import org.gudelker.parser.StreamingParser
import org.gudelker.parser.StreamingParserResult
import org.gudelker.rules.FormatterConfigLoader
import org.gudelker.rules.JsonReaderFormatterToMap
import org.gudelker.rules.YamlReaderFormatterToMap
import org.gudelker.sourcereader.FileSourceReader
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.StatementStream
import org.gudelker.utilities.Version
import java.io.File

fun parseVersion(version: String): Version =
    when (version) {
        "1.0" -> Version.V1
        "1.1" -> Version.V2
        else -> Version.V1
    }

fun showProgress(
    stage: String,
    percent: Int,
) {
    println("[$stage] Progress: $percent%")
}

class PrintScriptCli : CliktCommand("CLI for PrintScript: validate, execute, format or analyze source files") {
    override fun run() {}
}

class Validation : CliktCommand("validation") {
    private val filePath by argument("Source file to validate")
    private val version by option("--version", "-v").default("1.0")

    override fun run() {
        try {
            val lexer = lexSource(filePath, version)
            val statements = parseTokens(lexer, version)
            statements.forEachIndexed { idx, statement ->
                showProgress("Parsing statement ${idx + 1}/${statements.size}", ((idx + 1) * 100) / statements.size)
                println(statement)
            }
            showProgress("Parsing", 90)
            showProgress("Validation", 100)
            echo("✅ Validation passed for version $version")
        } catch (e: Exception) {
            echo("❌ Error: ${e.message}", err = true)
        }
    }
}

private fun lexSource(
    filePath: String,
    version: String,
): StreamingLexer {
    showProgress("Lexing", 20)
    showProgress("Lexing", 20)
    val lexer = LexerFactory.createLexer(parseVersion(version))
    val streamingLexer = StreamingLexer(lexer)
    val sourceReader = FileSourceReader(filePath)
    streamingLexer.initialize(sourceReader)
    return streamingLexer
}

private fun parseTokens(
    streamingLexer: StreamingLexer,
    version: String,
): List<Statement> {
    showProgress("Parsing", 50)
    val parser = DefaultParserFactory.createParser(parseVersion(version))
    val streamingParser = StreamingParser(parser)
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
            is StreamingParserResult.StatementParsed -> {
                statements.add(parseResult.statement)
            }
            is StreamingParserResult.Error -> {
                if (parseResult.message.lowercase().contains("need more tokens")) {
                    continue
                }
                throw Exception("Parse error: ${parseResult.message}")
            }
            is StreamingParserResult.Finished -> {
                break
            }
        }
    }

    return statements
}

class Execution : CliktCommand("execution") {
    private val filePath by argument("Source file to execute")
    private val version by option("--version", "-v").default("1.0")

    override fun run() {
        try {
            val lexer = LexerFactory.createLexer(parseVersion(version))
            val parser = DefaultParserFactory.createParser(parseVersion(version))
            val interpreter = ChunkBaseFactory.createInterpreter(parseVersion(version), CLIInputProvider())
            val evaluators = interpreter.getEvaluators()
            val streamingLexer = StreamingLexer(lexer)
            val streamingParser = StreamingParser(parser)
            val streamingInterpreter = StreamingInterpreter(evaluators)
            val pipeline = StreamingPipeline(streamingLexer, streamingParser, streamingInterpreter)
            val sourceReader = FileSourceReader(filePath)
            pipeline.initialize(sourceReader)
            pipeline.processAll()
            val errorMessage = pipeline.getLastErrorMessage()
            if (errorMessage == null) {
                echo("❌ Error: $errorMessage", err = true)
            } else {
                showProgress("Executing", 100)
                echo("✅ Execution finished")
            }
        } catch (e: Exception) {
            echo("❌ Error: ${e.message}", err = true)
        }
    }
}

class Formatting : CliktCommand("formatting") {
    private val filePath by argument("Source file to format")
    private val version by option("--version", "-v").default("1.0")
    private val configPath by option("--config").required()

    override fun run() {
        try {
            val lexer = lexSource(filePath, version)
            val statements = parseTokens(lexer, version)
            showProgress("Formatting", 100)
            val formatter = DefaultFormatterFactory.createFormatter(parseVersion(version))
            val configLoader = getFormatterConfigLoader(configPath)
            val strBuilder = StringBuilder()
            for (statement in statements) {
                val formatted = formatter.format(statement, configLoader.loadConfig())
                strBuilder.append(formatted)
            }
            File(filePath).writeText(strBuilder.toString())
            echo("✅ Archivo formateado correctamente: $filePath")
        } catch (e: Exception) {
            echo("❌ Error: $e", err = true)
        }
    }
}

private fun getFormatterConfigLoader(configPath: String): FormatterConfigLoader {
    return when (File(configPath).extension.lowercase()) {
        "yaml", "yml" -> YamlReaderFormatterToMap(configPath)
        else -> JsonReaderFormatterToMap(configPath)
    }
}

class Analyzing : CliktCommand("analyzing") {
    private val filePath by argument("Source file to analyze")
    private val version by option("--version", "-v").default("1.0")
    private val configPath by option("--config").required()

    override fun run() {
        try {
            val lexer = lexSource(filePath, version)
            val statements = parseTokens(lexer, version)
            showProgress("Analyzing", 100)
            val linter = DefaultLinterFactory.createLinter(parseVersion(version))
            val statementStream = StatementStream(statements)
            val configLoader = getLinterConfigLoader(configPath)
            val rules = configLoader.loadConfig()
            val result = linter.lint(statementStream, rules)
            val lintResults = result.results
            if (lintResults.isEmpty()) {
                echo("✅ No lint violations found. All statements passed analysis successfully.")
            } else {
                for (violation in lintResults) {
                    val pos = violation.position
                    echo(
                        "❌ Lint error: ${violation.message} " +
                            "[Lines ${pos.startLine}-${pos.endLine}, Columns ${pos.startColumn}-${pos.endColumn}]",
                    )
                }
            }
        } catch (e: Exception) {
            echo("❌ Error: ${e.message}", err = true)
        }
    }
}

private fun getLinterConfigLoader(configPath: String): LinterConfigLoader {
    return when (File(configPath).extension.lowercase()) {
        "yaml", "yml" -> YamlLinterConfigLoaderToMap(configPath)
        else -> JsonLinterConfigLoaderToMap(configPath)
    }
}

fun main(args: Array<String>) =
    PrintScriptCli()
        .subcommands(Validation(), Execution(), Formatting(), Analyzing())
        .main(args)
