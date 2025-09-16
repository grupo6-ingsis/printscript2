package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class SingleSpaceSeparationRule : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "mandatory-single-space-separation"
        val rule = formatterRuleMap[ruleName] ?: return false
        return rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val rule = formatterRuleMap["mandatory-single-space-separation"] ?: return string
        val spaceQuantity = rule.quantity
        val space = " ".repeat(spaceQuantity)

        var inString = false
        val processed = StringBuilder()

        var i = 0
        while (i < string.length) {
            val char = string[i]
            if (char == '"' || char == '\'') {
                inString = !inString
                processed.append(char)
                i++
                continue
            }

            if (inString) {
                processed.append(char)
                i++
                continue
            }

            when (char) {
                ':', '=', '(', ')', '{', '}', '[', ']', '+', '-', '*', '/', '%', ',' -> {
                    if (processed.isNotEmpty() && processed.last() != ' ' && processed.last() != '\n') {
                        processed.append(space)
                    }
                    processed.append(char)
                    if (i < string.length - 1 && !string[i + 1].isWhitespace() &&
                        string[i + 1] != ')' && string[i + 1] != ';'
                    ) {
                        processed.append(space)
                    }
                    i++
                }
                ';' -> {
                    processed.append(char)

                    if (i < string.length - 1 && string[i + 1] != '\n') {
                        processed.append(space)
                    }
                    i++
                }
                ' ', '\t' -> {
                    if (processed.isEmpty() || (processed.last() != ' ' && processed.last() != '\n')) {
                        processed.append(space)
                    }

                    while (i < string.length - 1 && (string[i + 1] == ' ' || string[i + 1] == '\t')) {
                        i++
                    }
                    i++
                }
                '\n' -> {
                    processed.append(char)
                    i++
                }
                else -> {
                    processed.append(char)
                    i++
                }
            }
        }

        return processed.toString()
    }
}
