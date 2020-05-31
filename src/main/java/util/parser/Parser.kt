package util.parser

import data.*
import data.TokenType.*

/**
 * Constructor
 * @param tokens - List of tokens of the expression
 */
class Parser(private val tokens: List<Token>) {
    /**
     * field denoting current position in list tokens
     */
    private var pos = 0

    fun parse(): ExprNode {
        var e1 = addend()
        var token: Token?
        while (true) {
            token = match(ADD, SUB)
            if (token != null) {
                val e2 = addend()
                e1 = BinOpNode(token, e1, e2)
                continue
            }
            break
        }
        return e1
    }

    private fun addend(): ExprNode {
        var e1 = multiplier()
        var token: Token?
        while (true) {
            token = match(MUL, DIV)
            if (token != null) {
                val e2 = multiplier()
                e1 = BinOpNode(token, e1, e2)
                continue
            }
            break
        }
        return e1
    }

    private fun multiplier(): ExprNode {
        return if (match(LPAR) != null) {
            val e = parse()
            require(RPAR)
            e
        } else {
            elem()
        }
    }

    private fun elem(): ExprNode {
        val num = match(NUMBER)
        if (num != null) return NumberNode(num)
        val id = match(ID)
        if (id != null) return VarNode(id)
        error("Ожидается число или переменная")
    }

    private fun match(vararg types: TokenType): Token? {
        if (pos < tokens.size) {
            val token: Token = tokens[pos]
            if (types.contains(token.type)) {
                pos++
                return token
            }
        }
        return null
    }

    /**
     * @param types - one or more token types
     * @return match token in tokens
     */
    private fun require(vararg types: TokenType): Token {
        return match(*types) ?: error("Ожидался ${types.contentToString()}")
    }
    /**
     * throw RuntimeException with your message
     * shows position in expression
     */
    private fun error(message: String): Nothing {
        if (pos < tokens.size) {
            throw RuntimeException(message +
                    " в строке - ${tokens[pos - 1].line}," +
                    " позиции - ${tokens[pos - 1].column}")
        } else {
            throw RuntimeException("$message в конце выражения")
        }
    }
}