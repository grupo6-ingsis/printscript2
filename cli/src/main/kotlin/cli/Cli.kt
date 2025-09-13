package org.gudelker.cli
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import org.gudelker.DefaultFormatterFactory
import org.gudelker.DefaultLinterFactory
import org.gudelker.InterpreterFactory
import org.gudelker.LexerFactory
import org.gudelker.StatementStream
import org.gudelker.linterloader.JsonLinterConfigLoaderToMap
import org.gudelker.parser.DefaultParserFactory
import org.gudelker.result.LexerSyntaxError
import org.gudelker.result.ParserSyntaxError
import org.gudelker.result.Valid
import org.gudelker.result.ValidTokens
import org.gudelker.rules.JsonReaderFormatterToMap
import org.gudelker.sourcereader.FileSourceReader
import org.gudelker.tokenstream.TokenStream
import org.gudelker.utilities.Version

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
            val tokens = lexSource(filePath, version)
            val ast = parseTokens(tokens, version)
            val statements = ast.getStatements()
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
): ValidTokens {
    showProgress("Lexing", 20)
    val lexer = LexerFactory.createLexer(parseVersion(version))
    val sourceReader = FileSourceReader(filePath)
    val tokensResult = lexer.lex(sourceReader)
    return when (tokensResult) {
        is ValidTokens -> tokensResult
        is LexerSyntaxError -> throw Exception("Lexing error: ${tokensResult.messageError}")
    }
}

private fun parseTokens(
    tokens: ValidTokens,
    version: String,
): Valid {
    showProgress("Parsing", 50)
    val parser = DefaultParserFactory.createParser(parseVersion(version))
    val tokenIterator = TokenStream(tokens.value)
    val ast = parser.parse(tokenIterator)
    return when (ast) {
        is Valid -> ast
        is ParserSyntaxError -> throw Exception("Parse error: ${ast.getError()}")
        else -> throw Exception("Unknown parser result")
    }
}

class Execution : CliktCommand("execution") {
    private val filePath by argument("Source file to execute")
    private val version by option("--version", "-v").default("1.0")

    override fun run() {
        try {
            val tokens = lexSource(filePath, version)
            val ast = parseTokens(tokens, version)
            showProgress("Executing", 100)
            val interpreter = InterpreterFactory.createInterpreter(parseVersion(version))
            interpreter.interpret(ast.getStatements())
            echo("✅ Execution finished")
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
            val tokens = lexSource(filePath, version)
            val ast = parseTokens(tokens, version)
            showProgress("Formatting", 100)
            val formatter = DefaultFormatterFactory.createFormatter(parseVersion(version))
            val jsonToMap = JsonReaderFormatterToMap(configPath)
            val strBuilder = StringBuilder()
            for (statement in ast.getStatements()) {
                formatter.format(statement, jsonToMap.loadConfig())
                strBuilder.append(statement)
            }
            echo(strBuilder.toString())
        } catch (e: Exception) {
            echo("❌ Error: ${e.message}", err = true)
        }
    }
}

class Analyzing : CliktCommand("analyzing") {
    private val filePath by argument("Source file to analyze")
    private val version by option("--version", "-v").default("1.0")
    private val configPath by option("--config").required()

    override fun run() {
        try {
            val tokens = lexSource(filePath, version)
            val ast = parseTokens(tokens, version)
            showProgress("Analyzing", 100)
            val linter = DefaultLinterFactory.createLinter(parseVersion(version))
            val statementStream = StatementStream(ast.getStatements())
            val configLoader = JsonLinterConfigLoaderToMap(configPath)
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

fun main(args: Array<String>) =
    PrintScriptCli()
        .subcommands(Validation(), Execution(), Formatting(), Analyzing())
        .main(args)
