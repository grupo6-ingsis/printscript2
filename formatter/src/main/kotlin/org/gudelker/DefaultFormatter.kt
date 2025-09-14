package org.gudelker

import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.parser.tokenstream.TokenStream
import org.gudelker.rules.FormatterRule

class DefaultFormatter : Formatter {
    override fun format(
        tokenStream: TokenStream,
        rules: Map<String, FormatterRule>,
    ): String {
        val result = StringBuilder()
        val tokens = tokenStream.getTokens()

        for (i in tokens.indices) {
            val current = tokens[i]
            val previous = if (i > 0) tokens[i - 1] else null
            val next = if (i < tokens.size - 1) tokens[i + 1] else null

            when (current.getType()) {
                TokenType.COLON -> {
                    handleColon(result, current, previous, next, rules)
                }
                TokenType.ASSIGNATION -> {
                    handleEquals(result, current, previous, next, rules)
                }
                TokenType.FUNCTION -> {
                    handleFunction(result, current, rules)
                }
                TokenType.SEMICOLON -> {
                    result.append(current.getValue())
                    result.append("\n") // Salto de línea obligatorio después de ;
                }
                TokenType.OPERATOR -> {
                    handleOperator(result, current, previous, next)
                }
                TokenType.EOF -> {
                    // No hacer nada con EOF
                }
                else -> {
                    result.append(current.getValue())
                    // Añadir espacios originales hasta el siguiente token si no es un caso especial
                    addOriginalSpaces(result, current, next)
                }
            }
        }

        return result.toString()
    }

    private fun handleColon(
        result: StringBuilder,
        current: Token,
        previous: Token?,
        next: Token?,
        rules: Map<String, FormatterRule>,
    ) {
        val beforeRule = rules["enforce-spacing-before-colon-in-declaration"]
        if (beforeRule?.on == true && previous != null) {
            removeTrailingSpaces(result)
            result.append(" ".repeat(beforeRule.quantity))
        }

        result.append(current.getValue())

        val afterRule = rules["enforce-spacing-after-colon-in-declaration"]
        if (afterRule?.on == true && next != null) {
            result.append(" ".repeat(afterRule.quantity))
        }
    }

    private fun handleEquals(
        result: StringBuilder,
        current: Token,
        previous: Token?,
        next: Token?,
        rules: Map<String, FormatterRule>,
    ) {
        val rule = rules["enforce-spacing-around-equals"]
        if (rule?.on == true) {
            val targetSpaces = rule.quantity

            // Espacios antes del =
            if (previous != null) {
                removeTrailingSpaces(result)
                result.append(" ".repeat(targetSpaces))
            }

            result.append(current.getValue())

            // Espacios después del =
            if (next != null) {
                result.append(" ".repeat(targetSpaces))
            }
        } else {
            // Mantener espacios originales
            if (previous != null) {
                val spacesBefore = calculateSpacesBefore(current, previous)
                if (spacesBefore > 0) {
                    removeTrailingSpaces(result)
                    result.append(" ".repeat(spacesBefore))
                }
            }

            result.append(current.getValue())

            if (next != null) {
                val spacesAfter = calculateSpacesAfter(current, next)
                result.append(" ".repeat(spacesAfter))
            }
        }
    }

    private fun handleFunction(
        result: StringBuilder,
        current: Token,
        rules: Map<String, FormatterRule>,
    ) {
        if (current.getValue() == "println") {
            val rule = rules["line-breaks-after-println"]
            if (rule?.on == true) {
                val lineBreaks = rule.quantity
                result.append("\n".repeat(lineBreaks))
            }
        }
        result.append(current.getValue())
    }

    private fun handleOperator(
        result: StringBuilder,
        current: Token,
        previous: Token?,
        next: Token?,
    ) {
        // Siempre un espacio antes y después de operadores
        if (previous != null) {
            removeTrailingSpaces(result)
            result.append(" ")
        }
        result.append(current.getValue())
        if (next != null) {
            result.append(" ")
        }
    }

    private fun calculateSpacesBefore(
        current: Token,
        previous: Token,
    ): Int {
        return (current.getPosition().startColumn - previous.getPosition().endColumn).coerceAtLeast(0)
    }

    private fun calculateSpacesAfter(
        current: Token,
        next: Token,
    ): Int {
        return (next.getPosition().startColumn - current.getPosition().endColumn).coerceAtLeast(0)
    }

    private fun removeTrailingSpaces(result: StringBuilder) {
        while (result.isNotEmpty() && result.last() == ' ') {
            result.deleteCharAt(result.length - 1)
        }
    }

    private fun addOriginalSpaces(
        result: StringBuilder,
        current: Token,
        next: Token?,
    ) {
        if (next != null &&
            next.getType() != TokenType.COLON &&
            next.getType() != TokenType.ASSIGNATION &&
            next.getType() != TokenType.OPERATOR &&
            next.getType() != TokenType.SEMICOLON
        ) {
            val spaces = calculateSpacesAfter(current, next)
            val maxSpaces = minOf(spaces, 1) // Máximo un espacio entre tokens
            result.append(" ".repeat(maxSpaces))
        }
    }
}
