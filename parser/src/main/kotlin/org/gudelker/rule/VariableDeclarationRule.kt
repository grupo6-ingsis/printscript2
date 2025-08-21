package org.gudelker.rule

import org.example.org.gudelker.ExpressionStatement
import org.example.org.gudelker.VariableDeclaration
import org.gudelker.Token
import org.gudelker.components.org.gudelker.TokenType
import org.gudelker.result.IndexOutOfBounds
import org.gudelker.result.Result
import org.gudelker.result.SyntaxError
import org.gudelker.result.ValidStatementResult

class VariableDeclarationRule(private val keywords: Set<String>,
  private val expressionRule: SyntaxRule
  ) : SyntaxRule {
  override fun matches(
    tokens: List<Token>,
    index: Int,
  ): Boolean {
    return isFirstTokenKeyword(tokens, index) && isValueInKeywordsSet(tokens[index].getValue(), keywords)
  }

  override fun parse(
    tokens: List<Token>,
    index: Int,
  ): Result {
    if (isNextIndexOutOfBounds(index, tokens)) {
      return IndexOutOfBounds("Index out of tokenList bounds at index: $index")
    }

    if (!isTokenAtIndexIdentifier(tokens, index + 1)) {
      return SyntaxError(
        "Se esperaba un identificador después de 'let' en la fila: " +
          tokens[index].getPosition().startLine,
      )
    }

    val identifier = tokens[index + 1].getValue()
    var currentIndex = index + 2

    var type: String? = null
    if (currentIndex < tokens.size && isTokenAtIndexColon(tokens, currentIndex)) {
      if (isNextIndexOutOfBounds(currentIndex, tokens)) {
        return IndexOutOfBounds("Index out of tokenList bounds at index: $currentIndex")
      }

      if (isTokenAtIndexType(tokens, currentIndex)) {
        return SyntaxError("Se esperaba un tipo después de ':'")
      }

      type = tokens[currentIndex + 1].getValue()
      currentIndex += 2
    }
    // Verificar asignación
    if (currentIndex >= tokens.size || tokens[currentIndex].getType() != TokenType.ASSIGNATION) {
      return SyntaxError("Se esperaba '=' después de la declaración")
    }
    currentIndex += 1
    if(isTokenAtIndexColon(tokens, currentIndex)) {
      return SyntaxError("Se esperaba una expresión después de '='")
    }

    when (val valueExpr = expressionRule.parse(tokens, currentIndex)) {
      is ValidStatementResult -> {
        val expressionStatement = valueExpr.getStatement() as ExpressionStatement
        val statement =
          VariableDeclaration(
            identifier,
            type,
            expressionStatement,
          )
        currentIndex++

        if (tokens[currentIndex].getType() != TokenType.SEMICOLON) {
          return SyntaxError("Se esperaba un punto y coma al final de la declaración")
        }
        currentIndex++
        return ValidStatementResult(statement, currentIndex)
      }
      else -> {
        return SyntaxError("Error al parsear la expresión")
      }
    }
  }

  private fun isTokenAtIndexType(
    tokens: List<Token>,
    currentIndex: Int,
  ) = tokens[currentIndex + 1].getType() != TokenType.TYPE

  private fun isTokenAtIndexColon(
    tokens: List<Token>,
    currentIndex: Int,
  ) = tokens[currentIndex].getType() == TokenType.COLON

  private fun isTokenAtIndexIdentifier(
    tokens: List<Token>,
    index: Int,
  ) = tokens[index].getType() == TokenType.IDENTIFIER

  private fun isNextIndexOutOfBounds(
    index: Int,
    tokens: List<Token>,
  ): Boolean {
    return index + 1 >= tokens.size
  }

  private fun isFirstTokenKeyword(
    tokens: List<Token>,
    index: Int,
  ): Boolean {
    val type = tokens[index].getType()
    return type == TokenType.KEYWORD
  }

  private fun isValueInKeywordsSet(
    value: String,
    keywords: Set<String>,
  ): Boolean {
    return keywords.contains(value)
  }

  private fun isTokenAtIndexSemicolon(
    tokens: List<Token>,
    index: Int,
  ): Boolean {
    return tokens[index].getType() == TokenType.SEMICOLON
  }
}



