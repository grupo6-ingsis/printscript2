package org.gudelker.expressions

import org.gudelker.statements.interfaces.ConditionalStatement
import org.gudelker.statements.interfaces.Statement
import org.gudelker.stmtposition.ComboValuePosition

data class ConditionalExpression(
    val ifKeyword: ComboValuePosition<String>,
    val condition: BooleanExpressionStatement,
    val ifBody: List<Statement>,
    val elseBody: List<Statement>? = null,
) : ConditionalStatement
