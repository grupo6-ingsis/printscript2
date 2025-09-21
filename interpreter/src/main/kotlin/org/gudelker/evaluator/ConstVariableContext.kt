package org.gudelker.evaluator

data class ConstVariableContext(
    private val constants: Map<String, Any> = emptyMap(),
    private val variables: Map<String, Any?> = emptyMap(),
    private val variableTypes: Map<String, String> = emptyMap(),
) {
    fun setConstant(
        name: String,
        value: Any,
    ): ConstVariableContext {
        if (hasConstant(name)) {
            throw Exception("Constante ya declarada: $name")
        }
        return copy(constants = constants + (name to value))
    }

    fun setVariableWithType(
        name: String,
        value: Any?,
        type: String,
    ): ConstVariableContext {
        return copy(
            variables = variables + (name to value),
            variableTypes = variableTypes + (name to type),
        )
    }

    fun getVariableType(name: String): String? = variableTypes[name]

    fun getVariable(name: String): Any {
        if (!hasVariable(name)) {
            throw Exception("Variable no declarada: $name")
        } else {
            return variables[name]!! // !! para decir que no es nulo.
        }
    }

    fun getConstant(name: String): Any {
        if (!hasConstant(name)) {
            throw Exception("Variable no declarada: $name")
        } else {
            return constants[name]!! // !! para decir que no es nulo.
        }
    }

    fun updateVariable(
        name: String,
        value: Any,
    ): ConstVariableContext {
        if (!variables.containsKey(name)) {
            throw Exception("Variable no declarada: $name")
        }
        return copy(variables = variables + (name to value))
    }

    fun hasVariable(name: String): Boolean = variables.containsKey(name)

    fun hasConstant(name: String): Boolean = constants.containsKey(name)
}
