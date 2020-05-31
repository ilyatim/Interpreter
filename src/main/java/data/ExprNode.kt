package data

/**
 * wrapper class
 */
sealed class ExprNode

/**
 * data class
 * @constructor
 * @param op - operation with left and right node
 * @param op - data.Token stored data.TokenType, value, index in expression, line in expression, column in expression
 */
data class BinOpNode(var op: Token, var left: ExprNode, var right: ExprNode) : ExprNode()
/**
 * data class
 * @constructor
 * @param number - data.Token stored data.TokenType, value, index in expression, line in expression, column in expression
 */
data class NumberNode(var number: Token) : ExprNode()
/**
 * data class
 * @constructor
 * @param id - data.Token stored data.TokenType, value, index in expression, line in expression, column in expression
 */
data class VarNode(var id: Token) : ExprNode()
///**
// * data class
// * @constructor
// * @param number - data.ExprNode that can stored another Node (e.g. data.NumberNode)
// */
//data class NegativeNumberNode(var number: data.ExprNode) : data.ExprNode()