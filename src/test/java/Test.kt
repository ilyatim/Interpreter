import data.ExprNode
import data.Token
import data.TokenType
import org.junit.Test
import util.Lexer
import util.collector.Compiler
import util.collector.Compiler.Companion.foldConstants
import util.collector.Interpreter
import util.parser.Parser
import java.io.ByteArrayInputStream

import java.io.InputStream





class Test {
    @Test
    fun testInterpreter() {
        val text = "x + 20 * (3 + 1)"

        val l = Lexer(text)
        val tokens: MutableList<Token> = l.getLex()
        tokens.removeIf { t: Token -> t.type === TokenType.SPACE }

        val p = Parser(tokens)
        val node: ExprNode = p.parse()
        val inputVariable = "5"
        val input: InputStream = ByteArrayInputStream(inputVariable.toByteArray())
        System.setIn(input)
        val result: Int = Interpreter.eval(node)
        println(result)
    }
    @Test
    fun testCompiler() {
        val text = "x + 20 * (3 + y)"

        val l = Lexer(text)
        val tokens: MutableList<Token> = l.getLex()
        tokens.removeIf { t: Token -> t.type === TokenType.SPACE }

        val p = Parser(tokens)
        val node: ExprNode = p.parse()
        val optimizedNode: ExprNode = foldConstants(node)

        val compiler = Compiler()
        compiler.compile32(optimizedNode)
        compiler.outputWithPeepholeOptimization()
    }
    @Test
    fun testLexer() {

    }
    @Test
    fun testObj() {

    }
    @Test
    fun testParser() {

    }
}