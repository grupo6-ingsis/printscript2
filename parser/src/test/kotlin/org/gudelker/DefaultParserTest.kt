package org.gudelker

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.parser.DefaultParser
import org.gudelker.result.Valid
import org.gudelker.rule.BinaryRule
import org.gudelker.rule.ExpressionRule
import org.gudelker.rule.LiteralNumberRule
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
        println("1+(5*6/10)")
        print(statements[0])
    }
}
