package org.example.org.gudelker

data class VariableDeclaration(
  val identifier: String,
  val type: String?,
  val value: ExpressionStatement,
) : VariableStatement
