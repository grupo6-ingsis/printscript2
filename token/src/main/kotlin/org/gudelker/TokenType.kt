package org.gudelker.components.org.gudelker

enum class TokenType {
    TYPE,

    COLON, // :
    ASSIGNATION, // =
    KEYWORD, // class, import, package, let, const, var, return, etc.
    FUNCTIONKEYWORD, // if, else, while, for, etc.
    FUNCTION, // printf(), println(), read(), etc.

    STRING,
    NUMBER,
    CHARACTER,

    OPERATOR,
    SEMICOLON,
    WHITESPACE,
    NEWLINE,
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
