package org.gudelker

import org.example.org.gudelker.Statement
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.Valid
import org.gudelker.rule.LiteralNumberRule
import org.gudelker.rule.VariableDeclarationRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DefaultParserTest {

    @Test
    fun `should parse simple statement`() {
        // Crear una sentencia de prueba
        val testStatement = object : Statement {
            override fun toString(): String = "TestStatement"
        }

        // Crear tokens de prueba
        val tokens = listOf(
            Token(TokenType.KEYWORD, "let", Position()),
            Token(TokenType.IDENTIFIER, "x", Position()),
            Token(TokenType.COLON, ":", Position()),
            Token(TokenType.TYPE, "Number", Position()),
            Token(TokenType.ASSIGNATION, "=", Position()),
            Token(TokenType.NUMBER, "10", Position()),
            Token(TokenType.SEMICOLON, ";", Position()),
            Token(TokenType.EOF, "", Position()),

        )
        assertTrue(false) // testing hook
        val variableDeclarationRule = VariableDeclarationRule()
        val literalNumberRule = LiteralNumberRule()

        // Crear el parser y ejecutar
        val parser = DefaultParser(tokens, emptyList(), listOf(variableDeclarationRule, literalNumberRule))
        val result = parser.parse()

        // Verificar resultado
        assertTrue(result is Valid)
        val statements = (result as Valid).getStatements()
        assertEquals(1, statements.size)
        print(statements[0])
    }


}