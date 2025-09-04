package org.gudelker.components.org.gudelker

enum class TokenType {
    TYPE,

    COLON, // :
    ASSIGNATION, // =
    KEYWORD, // class, import, package, let, const, var, return, etc.
    FUNCTION, // printf(), println(), read(), etc.

    STRING,
    NUMBER,
    BOOLEAN, // true, false

    OPERATOR,
    SEMICOLON,
    OPEN_PARENTHESIS,
    CLOSE_PARENTHESIS,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    COMPARATOR, // <, >, <=, >=, ==, !=

    IF_KEYWORD, // if
    ELSE_KEYWORD, // else

    IDENTIFIER, // variable name
    UNKNOWN, // Error

    EOF,
}
