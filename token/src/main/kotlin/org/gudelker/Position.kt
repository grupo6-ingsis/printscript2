package org.gudelker

data class Position (
    val startOffset: Int = 1, // Index of the first character of a token in the entire input string
    val endOffset: Int = 1, // Index of the last character of a token + 1
    val startLine: Int = 1,
    val endLine: Int = 1,
    val startColumn: Int = 1,
    val endColumn: Int = 1
) {

}