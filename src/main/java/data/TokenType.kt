package data

import java.util.regex.Pattern

enum class TokenType(regExp: String) {
    NUMBER("[0-9]+"),
    WHILE("while"),
    DONE("done"),
    DO("do"),
    PRINT("print"),
    ID("[a-zA-z]"),
    ADD("\\+"),
    SUB("-"),
    MUL("\\*"),
    DIV("/"),
    LPAR("\\("),
    RPAR("\\)"),
    MOREEQUAL(">="),
    LESSEQUAL("<="),
    MORE(">"),
    LESS("<"),
    TWOEQUAL("=="),
    EQUAL("="),
    NOTEQUAL("!="),
    SPACE("[ \t\r\n]+"),
    SEMICOLON(";");
    val pattern: Pattern = Pattern.compile(regExp)
}