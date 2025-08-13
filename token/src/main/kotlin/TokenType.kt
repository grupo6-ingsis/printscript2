package org.gudelker.components

enum class TokenType {
    TYPE,

    DECLARATION, // :
    ASSIGNATION, // =
    KEYWORD, // class, import, package, let, const, var, return, etc.
    FUNCTIONKEYWORD, // if, else, while, for, etc.
    FUNCTION, // printf(), println(), read(), etc.

    STRING,
    INTEGER,
    DOUBLE,
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
    UNKNOWN // Error
}