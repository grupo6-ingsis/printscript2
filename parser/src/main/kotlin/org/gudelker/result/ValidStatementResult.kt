package org.gudelker.result

import org.example.org.gudelker.Statement

class ValidStatementResult(
  private val statement: Statement,
  private val index: Int,
) : Result {
  fun getStatement(): Statement = statement

  fun getIndex(): Int = index
}
