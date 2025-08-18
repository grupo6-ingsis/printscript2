package org.gudelker.components.org.gudelker

enum class TokenType {
    TYPE,

    DECLARATION, // :
    ASSIGNATION, // =
    KEYWORD, // class, import, package, let, const, var, return, etc.
    FUNCTIONKEYWORD, // if, else, while, for, etc.
    FUNCTION, // printf(), println(), read(), etc.

    STRING,
    NUMBER,
    STRING_TYPE,
    NUMBER_TYPE,
    BOOLEAN,
    CHARACTER,

    OPERATOR,
    SEMICOLON,
    WHITESPACE,
    NEWLINE,
    PARENTHESIS,
    BRACKET,
    COMPARATOR, // <, >, <=, >=, ==, !=

    IDENTIFIER, // variable name
    UNKNOWN, // Error

    EOF
}