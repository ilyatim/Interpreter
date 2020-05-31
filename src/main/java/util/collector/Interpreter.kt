package util.collector

import data.*
import java.lang.Integer
import java.util.*


object Interpreter {
    fun eval(node: ExprNode): Int {
       when (node) {
            is NumberNode -> {
                return Integer.parseInt(node.number.text)
            }
            is BinOpNode  -> {
                val l = eval(node.left)
                val r = eval(node.right)
                when (node.op.type) {
                    TokenType.ADD -> l + r
                    TokenType.SUB -> l - r
                    TokenType.MUL -> l * r
                    TokenType.DIV -> {
                        if (r == 0) {
                            throw ArithmeticException("Деление на ноль в строке - " +
                                    "${node.op.line}, позиции - " +
                                    "${node.op.column}")
                        } else {
                            return l / r
                        }
                    }
                    else -> {
                        throw IllegalArgumentException("Неверный TokenType в строке - " +
                                "${node.op.line}, позиции - " +
                                "${node.op.column}")
                    }
                }
            }
           is VarNode -> {
               println("Введите значение ${node.id.text}:")
               val line = Scanner(System.`in`).nextLine()
               return line.toInt()
           }
        }
        throw IllegalStateException()
    }
}