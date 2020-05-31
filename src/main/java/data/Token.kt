package data

/**
 * @constructor
 * @param type - data.TokenType of current data.Token
 * @param text - value stored in data.Token
 * @param index - index in expression
 * @param line - row in expression
 * @param column - column in expression
 */
data class Token(val type: TokenType, val text: String, var index: Int, var line: Int, var column: Int)