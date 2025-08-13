package org.gudelker.lexer

import org.gudelker.components.TokenType
import java.util.EnumMap

object TokenRegex {
    private val patterns: EnumMap<TokenType, String> = EnumMap(TokenType::class.java)

    init {
        regex()
    }

    private fun regex() {
        // Basic elements
        patterns[TokenType.WHITESPACE] = "\\s+"
        patterns[TokenType.NEWLINE] = "\\r?\\n"
        patterns[TokenType.IDENTIFIER] = "[a-zA-Z_][a-zA-Z0-9_]*"

        // Literals
        patterns[TokenType.INTEGER] = "\\b\\d+\\b"
        patterns[TokenType.DOUBLE] = "\\b\\d+\\.\\d+\\b"
        patterns[TokenType.STRING] = "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\""
        patterns[TokenType.CHARACTER] = "'([^'\\\\]|\\\\[btnfr\"'\\\\])'"
        patterns[TokenType.BOOLEAN] = "\\b(true|false)\\b"

        // Keywords and types
        patterns[TokenType.TYPE] = "\\b(Int|Double|String|Boolean|Char)\\b"
        patterns[TokenType.KEYWORD] = "\\b(class|import|package|let|const|var|val|return)\\b"
        patterns[TokenType.FUNCTIONKEYWORD] = "\\b(if|else|when|while|for|do)\\b"
        patterns[TokenType.FUNCTION] = "\\b[a-zA-Z_][a-zA-Z0-9_]*(?=\\s*\\()"

        // Operators and syntax
        patterns[TokenType.DECLARATION] = ":"
        patterns[TokenType.ASSIGNATION] = "="
        patterns[TokenType.OPERATOR] = "\\+\\+|--|\\*\\*|[+\\-*/&|^%]"
        patterns[TokenType.COMPARATOR] = "<=|>=|==|!=|<|>"
        patterns[TokenType.SEMICOLON] = ";"
        patterns[TokenType.PARENTHESIS] = "[()]"
        patterns[TokenType.BRACKET] = "[\\[\\]{}]"
    }

    fun getPattern(type: TokenType): String?{
        return patterns[type]
    }

    fun getAllPatterns(): Map<TokenType, String>{
        return patterns.toMap()
    }
}



