package org.gudelker

import org.gudelker.parser.DefaultParserFactory
import org.gudelker.parser.result.Valid
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.token.Position
import org.gudelker.token.Token
import org.gudelker.token.TokenType
import org.gudelker.utilities.Version
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.collections.get

class DefaultParserTest {
    @Test
    fun `should parse simple statement`() {
        // Crear tokens de prueba
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.COLON, ":", Position()),
                Token(TokenType.TYPE, "Number", Position()),
//                Token(TokenType.ASSIGNATION, "=", Position()),
//                Token(TokenType.NUMBER, "10", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        print(statements[0])
    }

    @Test
    fun `should parse binary statement`() {
        // Crear tokens de prueba
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "1", Position()),
                Token(TokenType.OPERATOR, "*", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.OPERATOR, "*", Position()),
                Token(TokenType.NUMBER, "6", Position()),
                Token(TokenType.OPERATOR, "/", Position()),
                Token(TokenType.NUMBER, "10", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("1+(5*6/10)") // just to differentiate
        print(statements[0])
    }

    @Test
    fun `should parse unary statement`() {
        // Crear tokens de prueba
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.OPERATOR, "-", Position()),
                Token(TokenType.NUMBER, "1", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        print(statements[0])
    }

    @Test
    fun `should parse mix statement`() {
        // Crear tokens de prueba
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.OPERATOR, "-", Position()),
                Token(TokenType.NUMBER, "1", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        print(statements[0])
    }

    @Test
    fun `should with more than one statement`() {
        // Crear tokens de prueba
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "1", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "y", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.OPERATOR, "-", Position()),
                Token(TokenType.NUMBER, "1", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V2)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(2, statements.size)
        for (statement in statements) {
            println(statement)
        }
    }

    @Test
    fun `should parse grouping statement`() {
        // Crear tokens de prueba para: let x = (5 + 3);
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                Token(TokenType.NUMBER, "3", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("Grouping (5 + 3):")
        print(statements[0])
    }

    @Test
    fun `should parse callable statement`() {
        // Crear tokens de prueba para: println(42);
        val tokens =
            listOf(
                Token(TokenType.FUNCTION, "println", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.NUMBER, "42", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("Callable println(42):")
        print(statements[0])
    }

    @Test
    fun `should parse callable with empty parameters`() {
        // Crear tokens de prueba para: let x = read();
        val tokens =
            listOf(
                Token(TokenType.FUNCTION, "println", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("Callable println():")
        print(statements[0])
    }

    @Test
    fun `should parse all statements`() {
        // No funciona todavía
        // Crear tokens de prueba
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.OPERATOR, "-", Position()),
                Token(TokenType.NUMBER, "1", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
//                Token(TokenType.FUNCTION, "println", Position()),
//                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
//                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
//                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        print(statements[0])
    }

    @Test
    fun `should parse binary string statement`() {
        // Crear tokens de prueba
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.STRING, "\"Hello\"", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                Token(TokenType.STRING, "\"World!\"", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        print(statements[0])
    }

    @Test
    fun `should parse simple statement with reassignation`() {
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.COLON, ":", Position()),
                Token(TokenType.TYPE, "Number", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "10", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "4", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(2, statements.size)
        println(statements[0])
        print(statements[1])
    }

    @Test
    fun `should parse this statements`() {
        //  let x = -10;
        //            let y = +5;
        //            println(x + y);
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.OPERATOR, "-", Position()),
                Token(TokenType.NUMBER, "10", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "y", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.FUNCTION, "println", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                Token(TokenType.IDENTIFIER, "y", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V1)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(3, statements.size)
        println(statements[0])
        println(statements[1])
        println(statements[2])
    }

    @Test
    fun `should with more than one statement v2`() {
        // Crear tokens de prueba
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.BOOLEAN, "true", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.KEYWORD, "const", Position()),
                Token(TokenType.IDENTIFIER, "y", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V2)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(2, statements.size)
        for (statement in statements) {
            println(statement)
        }
    }

    @Test
    fun `should parse boolean`() {
        // Crear tokens de prueba
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "const", Position()),
                Token(TokenType.IDENTIFIER, "y", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "1", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.OPERATOR, "*", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.COMPARATOR, "!=", Position()),
                Token(TokenType.NUMBER, "40", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V2)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        for (statement in statements) {
            println(statement)
        }
    }

    @Test
    fun `should parse if statement without else`() {
        // Crear tokens de prueba para: if (true) { let x = 5; }
        val tokens =
            listOf(
                Token(TokenType.IF_KEYWORD, "if", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.BOOLEAN, "true", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.OPEN_BRACKET, "{", Position()),
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.CLOSE_BRACKET, "}", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V2)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("If without else:")
        println(statements[0])
    }

    @Test
    fun `should parse if statement with else`() {
        // Crear tokens de prueba para: if (x == 10) { let y = 20; } else { let y = 30; }
        val tokens =
            listOf(
                Token(TokenType.IF_KEYWORD, "if", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.COMPARATOR, "==", Position()),
                Token(TokenType.NUMBER, "10", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.OPEN_BRACKET, "{", Position()),
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "y", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "20", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.CLOSE_BRACKET, "}", Position()),
                Token(TokenType.ELSE_KEYWORD, "else", Position()),
                Token(TokenType.OPEN_BRACKET, "{", Position()),
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "y", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "30", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.CLOSE_BRACKET, "}", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V2)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("If with else:")
        println(statements[0])
    }

    @Test
    fun `should parse if statement with multiple statements in blocks`() {
        // Crear tokens de prueba para: if (true) { let x = 5; const y = 10; println(x); } else { x = 0; }
        val tokens =
            listOf(
                Token(TokenType.IF_KEYWORD, "if", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.BOOLEAN, "true", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.OPEN_BRACKET, "{", Position()),
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.KEYWORD, "const", Position()),
                Token(TokenType.IDENTIFIER, "y", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "10", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.FUNCTION, "println", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.CLOSE_BRACKET, "}", Position()),
                Token(TokenType.ELSE_KEYWORD, "else", Position()),
                Token(TokenType.OPEN_BRACKET, "{", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "0", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.CLOSE_BRACKET, "}", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V2)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("If with multiple statements:")
        println(statements[0])
    }

    @Test
    fun `should parse if statement with complex boolean expression`() {
        // Crear tokens de prueba para: if (x * (y + 3) - 4 <= 30 * 6 + 5) { let result = true; }
        val tokens =
            listOf(
                Token(TokenType.IF_KEYWORD, "if", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.OPERATOR, "*", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.IDENTIFIER, "y", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                Token(TokenType.NUMBER, "3", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.OPERATOR, "-", Position()),
                Token(TokenType.NUMBER, "4", Position()),
                Token(TokenType.COMPARATOR, "<=", Position()),
                Token(TokenType.NUMBER, "30", Position()),
                Token(TokenType.OPERATOR, "*", Position()),
                Token(TokenType.NUMBER, "6", Position()),
                Token(TokenType.OPERATOR, "+", Position()),
                Token(TokenType.NUMBER, "5", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.OPEN_BRACKET, "{", Position()),
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "result", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.BOOLEAN, "true", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.CLOSE_BRACKET, "}", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParserFactory.createParser(Version.V2)
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("If with complex boolean expression:")
        println(statements[0])
    }

    @Test
    fun `should parse function call readInput`() {
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "userInput", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.FUNCTION, "readInput", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)
        val parser = DefaultParserFactory.createParser(Version.V2)
        val result = parser.parse(tokenStream)

        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("ReadInput function call:")
        print(statements[0])
    }

    @Test
    fun `should parse function call readEnv with argument`() {
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "path", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.FUNCTION, "readEnv", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.STRING, "\"PATH\"", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)
        val parser = DefaultParserFactory.createParser(Version.V2)
        val result = parser.parse(tokenStream)

        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("ReadEnv function call with argument:")
        print(statements[0])
    }

    @Test
    fun `should parse nested function calls`() {
        val tokens =
            listOf(
                Token(TokenType.FUNCTION, "println", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.FUNCTION, "readInput", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val tokenStream = TokenStream(tokens)
        val parser = DefaultParserFactory.createParser(Version.V2)
        val result = parser.parse(tokenStream)

        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        println("Nested function calls:")
        print(statements[0])
    }
}
