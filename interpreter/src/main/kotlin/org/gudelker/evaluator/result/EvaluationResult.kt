package org.gudelker.evaluator

data class EvaluationResult(
    val value: Any?,
    val context: ConstVariableContext,
)
