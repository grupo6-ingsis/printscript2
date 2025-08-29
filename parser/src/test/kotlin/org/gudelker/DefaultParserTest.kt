package org.gudelker

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.parser.DefaultParser
import org.gudelker.result.Valid
import org.gudelker.rule.BinaryRule
import org.gudelker.rule.CallableRule
import org.gudelker.rule.ExpressionRule
import org.gudelker.rule.GroupingRule
import org.gudelker.rule.LiteralNumberRule
import org.gudelker.rule.UnaryRule
import org.gudelker.rule.VariableDeclarationRule
import org.gudelker.tokenstream.TokenStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DefaultParserTest {
    @Test
    fun `should parse simple statement`() {
        // Crear tokens de prueba
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
//        Token(TokenType.COLON, ":", Position()),
//        Token(TokenType.TYPE, "Number", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.NUMBER, "10", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )
//        assertTrue(false) // testing hoo
        val variableDeclarationRule = VariableDeclarationRule(setOf("let"), ExpressionRule(listOf(LiteralNumberRule())))

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule))
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

        val literalNumberRule = LiteralNumberRule()
        val binaryRule = BinaryRule(literalNumberRule)
        val expressionRule = ExpressionRule(listOf(binaryRule, literalNumberRule))
        val variableDeclarationRule =
            VariableDeclarationRule(
                setOf("let"),
                expressionRule,
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule))
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

        val literalNumberRule = LiteralNumberRule()
        val unaryRule = UnaryRule(literalNumberRule)
//        val binaryRule = BinaryRule(literalNumberRule)
        val expressionRule = ExpressionRule(listOf(unaryRule, literalNumberRule))
        val variableDeclarationRule =
            VariableDeclarationRule(
                setOf("let"),
                expressionRule,
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule))
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

        val literalNumberRule = LiteralNumberRule()
        val unaryRule = UnaryRule(literalNumberRule)
        val expressionRuleForBinary = ExpressionRule(listOf(unaryRule, literalNumberRule))
        val binaryRule = BinaryRule(expressionRuleForBinary)
        val fullExpressionRule = ExpressionRule(listOf(binaryRule, unaryRule, literalNumberRule))
        val variableDeclarationRule =
            VariableDeclarationRule(
                setOf("let"),
                fullExpressionRule,
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule))
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

        val literalNumberRule = LiteralNumberRule()
        val binaryRule = BinaryRule(literalNumberRule)
        val expressionRule = ExpressionRule(listOf(binaryRule, literalNumberRule))
        val variableDeclarationRule =
            VariableDeclarationRule(
                setOf("let"),
                expressionRule,
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule))
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

        val literalNumberRule = LiteralNumberRule()
        val binaryRule = BinaryRule(literalNumberRule)
        val groupingRule = GroupingRule(ExpressionRule(listOf(binaryRule, literalNumberRule)))
        val expressionRule = ExpressionRule(listOf(groupingRule, binaryRule, literalNumberRule))
        val variableDeclarationRule =
            VariableDeclarationRule(
                setOf("let"),
                expressionRule,
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule))
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
        // Crear tokens de prueba para: let x = println(42);
        val tokens =
            listOf(
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.FUNCTION, "println", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.NUMBER, "42", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val literalNumberRule = LiteralNumberRule()
        val callableRule = CallableRule(ExpressionRule(listOf(literalNumberRule)))
        val expressionRule = ExpressionRule(listOf(callableRule, literalNumberRule))
        val variableDeclarationRule =
            VariableDeclarationRule(
                setOf("let"),
                expressionRule,
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule))
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
                Token(TokenType.KEYWORD, "let", Position()),
                Token(TokenType.IDENTIFIER, "x", Position()),
                Token(TokenType.ASSIGNATION, "=", Position()),
                Token(TokenType.FUNCTION, "println", Position()),
                Token(TokenType.OPEN_PARENTHESIS, "(", Position()),
                Token(TokenType.CLOSE_PARENTHESIS, ")", Position()),
                Token(TokenType.SEMICOLON, ";", Position()),
                Token(TokenType.EOF, "", Position()),
            )

        val literalNumberRule = LiteralNumberRule()
        val callableRule = CallableRule(ExpressionRule(listOf(literalNumberRule)))
        val expressionRule = ExpressionRule(listOf(callableRule, literalNumberRule))
        val variableDeclarationRule =
            VariableDeclarationRule(
                setOf("let"),
                expressionRule,
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule))
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

        val literalNumberRule = LiteralNumberRule()
        val unaryRule = UnaryRule(literalNumberRule)
        val expressionRuleForBinary = ExpressionRule(listOf(unaryRule, literalNumberRule))
        val binaryRule = BinaryRule(expressionRuleForBinary)
        val callableRule = CallableRule(ExpressionRule(listOf(binaryRule, unaryRule, literalNumberRule)))
        val fullExpressionRule = ExpressionRule(listOf(callableRule, binaryRule, unaryRule, literalNumberRule))
        val variableDeclarationRule =
            VariableDeclarationRule(
                setOf("let"),
                fullExpressionRule,
            )

        val tokenStream = TokenStream(tokens)

        // Crear el parser y ejecutar
        val parser = DefaultParser(tokenStream, emptyList(), listOf(variableDeclarationRule))
        val result = parser.parse(tokenStream)

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        print(statements[0])
    }
}
