package org.gudelker.evaluator

data class VariableContext(
    private val variables: Map<String, Any> = emptyMap(),
) {
    fun setVariable(
        name: String,
        value: Any,
    ): VariableContext {
        return copy(variables = variables + (name to value))
    }

    fun getVariable(name: String): Any {
        if (!hasVariable(name)) {
            throw IllegalArgumentException("Variable no declarada: $name")
        } else {
            return variables[name]!! // !! para decir que no es nulo.
        }
    }

    fun updateVariable(
        name: String,
        value: Any,
    ): VariableContext {
        if (!variables.containsKey(name)) {
            throw IllegalArgumentException("Variable no declarada: $name")
        }
        return copy(variables = variables + (name to value))
    }

    fun hasVariable(name: String): Boolean = variables.containsKey(name)
}
