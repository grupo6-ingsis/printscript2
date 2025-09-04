package org.gudelker.evaluator

data class ConstVariableContext(
    private val constants: Map<String, Any> = emptyMap(),
    private val variables: Map<String, Any> = emptyMap(),
) {
    fun setConstant(
        name: String,
        value: Any,
    ): ConstVariableContext {
        if (hasConstant(name)) {
            throw IllegalArgumentException("Constante ya declarada: $name")
        }
        return copy(constants = constants + (name to value))
    }

    fun setVariable(
        name: String,
        value: Any,
    ): ConstVariableContext {
        return copy(variables = variables + (name to value))
    }

    fun getVariable(name: String): Any {
        if (!hasVariable(name)) {
            throw IllegalArgumentException("Variable no declarada: $name")
        } else {
            return variables[name]!! // !! para decir que no es nulo.
        }
    }

    fun getConstant(name: String): Any {
        if (!hasConstant(name)) {
            throw IllegalArgumentException("Variable no declarada: $name")
        } else {
            return constants[name]!! // !! para decir que no es nulo.
        }
    }

    fun updateVariable(
        name: String,
        value: Any,
    ): ConstVariableContext {
        if (!variables.containsKey(name)) {
            throw IllegalArgumentException("Variable no declarada: $name")
        }
        return copy(variables = variables + (name to value))
    }

    fun hasVariable(name: String): Boolean = variables.containsKey(name)

    fun hasConstant(name: String): Boolean = constants.containsKey(name)
}
