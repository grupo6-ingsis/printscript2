package org.gudelker.result

class IndexOutOfBounds(
  private val errorMessage: String,
) : Result {
  fun getError(): String = errorMessage
}
