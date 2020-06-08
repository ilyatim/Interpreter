package util.collector

import data.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.LinkedHashSet


class Compiler(val instructions: MutableList<String> = ArrayList()) {

    companion object {
        fun foldConstants(node: ExprNode): ExprNode {
            return when (node) {
                is BinOpNode -> {
                    val left = foldConstants(node.left)
                    val right = foldConstants(node.right)
                    val leftConst = getConstant(left)
                    val rightConst = getConstant(right)
                    when {
                        leftConst != null && rightConst != null -> {
                            val result: Double = when (node.op.type) {
                                TokenType.ADD -> leftConst.toDouble() + rightConst.toDouble()
                                TokenType.SUB -> leftConst.toDouble() - rightConst.toDouble()
                                TokenType.MUL -> leftConst.toDouble() * rightConst.toDouble()
                                TokenType.DIV -> leftConst.toDouble() / rightConst.toDouble()
                                else -> throw IllegalStateException("unknown error!")
                            }
                            NumberNode(Token(TokenType.NUMBER, result.toString(), node.op.index, node.op.line, node.op.column))
                        }
                        isConstant(leftConst, 0.0) && node.op.type === TokenType.ADD -> right
                        isConstant(rightConst, 0.0) && node.op.type === TokenType.ADD -> left
                        isConstant(leftConst, 1.0) && node.op.type === TokenType.MUL -> right
                        isConstant(rightConst, 1.0) && node.op.type === TokenType.MUL -> left
                        isConstant(rightConst, 0.0) && node.op.type === TokenType.SUB -> left
                        isConstant(rightConst, 1.0) && node.op.type === TokenType.DIV -> left
                        else -> BinOpNode(node.op, left, right)
                    }
                }
                else -> {
                    node
                }
            }
        }

        private fun getConstant(node: ExprNode): Double? {
            return if (node is NumberNode) {
                node.number.text.toDouble()
            } else {
                null
            }
        }

        private fun isConstant(constValue: Double?, value: Double): Boolean {
            return if (constValue != null) {
                constValue.toDouble().compareTo(value) == 0
            } else {
                false
            }
        }

        private fun gatherVariables(node: ExprNode, usedVariables: MutableSet<String>) {
            when (node) {
                is BinOpNode -> {
                    gatherVariables(node.left, usedVariables)
                    gatherVariables(node.right, usedVariables)
                }
                is VarNode -> {
                    usedVariables.add(node.id.text)
                }
            }
        }
    }

    fun outputWithPeepholeOptimization() {
        val pushPattern: Pattern = Pattern.compile("\\s*push (.+)")
        val popPattern: Pattern = Pattern.compile("\\s*pop (.+)")
        var i = 0
        while (i < instructions.size) {
            val instruction = instructions[i]
            if (i + 1 < instructions.size) {
                val nextInstruction = instructions[i + 1]
                val pushMatcher: Matcher = pushPattern.matcher(instruction)
                val popMatcher: Matcher = popPattern.matcher(nextInstruction)
                if (pushMatcher.matches() && popMatcher.matches()) {
                    val pushed: String = pushMatcher.group(1)
                    val popped: String = popMatcher.group(1)
                    println("    mov $popped, $pushed")
                    i++
                    i++
                    continue
                }
            }
            println(instruction)
            i++
        }
    }
    private fun emit0(code: String) {
        instructions.add(code)
    }

    private fun emit(code: String) {
        instructions.add("    $code")
    }

    private fun compileExpr(node: ExprNode) {
        when (node) {
            is NumberNode -> emit("push dword ${node.number.text}")
            is BinOpNode -> {
                compileExpr(node.left)
                compileExpr(node.right)
                emit("pop ebx")
                emit("pop eax")
                when (node.op.type) {
                    TokenType.ADD -> emit("add eax, ebx")
                    TokenType.SUB -> emit("sub eax, ebx")
                    TokenType.MUL -> emit("imul eax, ebx")
                    TokenType.DIV -> {
                        emit("mov edx, 0")
                        emit("idiv ebx")
                    }
                }
                emit("push eax")
            }
            is VarNode -> {
                emit("push dword ${node.id.text}")
            }
        }
    }

    //gen 32 asm code
    fun compile32(node: ExprNode) {
        val usedVariables = LinkedHashSet<String>()
        gatherVariables(node, usedVariables)
        systemMessage1()
        showUsedVariable(usedVariables)
        compileExpr(node)
        systemMessage2(usedVariables)
    }

    private fun systemMessage1() {
        emit0("section .text")
        emit("global _main")
        emit("extern _printf")
        emit("extern _scanf")
        emit0("_main:")
    }

    private fun showUsedVariable(usedVariables: LinkedHashSet<String>) {
        for (variable in usedVariables) {
            emit("push $variable@prompt")
            emit("call _printf")
            emit("pop ebx")
            emit("push $variable")
            emit("push scanf_format")
            emit("call _scanf")
            emit("pop ebx")
            emit("pop ebx")
        }
    }

    private fun systemMessage2(usedVariables: LinkedHashSet<String>) {
        emit("push message")
        emit("call _printf")
        emit("pop ebx")
        emit("pop ebx")
        emit("ret")
        emit0("")
        emit0("section .rdata")
        // Константа для вызова printf для вывода результата:
        emit0("message: db 'Result is %d', 10, 0")
        // Константа для вызова scanf:
        emit0("scanf_format: db '%d', 0")
        // Константы для вызова printf для приглашения ввода переменных:
        for (variable in usedVariables) {
            emit0("$variable@prompt: db 'Input $variable: ', 0")
        }
        emit0("")
        emit0("section .bss")
        for (variable in usedVariables) {
            emit0("$variable: resd 1")
        }
    }

}