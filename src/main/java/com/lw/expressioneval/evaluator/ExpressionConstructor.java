package com.lw.expressioneval.evaluator;

import com.lw.expressioneval.evaluator.enums.TokenType;
import com.lw.expressioneval.evaluator.expressions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that constructs and validates expression syntax tree.
 */
public class ExpressionConstructor {
    /**
     * Method for constructing syntax tree from a list of {@link ExpressionToken} tokens.
     *
     * @param tokens list of tokens
     * @return syntax tree
     * @throws IllegalStateException if the result of the expression is not a boolean
     */
    public static Exp constructTree(List<ExpressionToken> tokens) {
        List<ExpressionToken> expressionTokens = new ArrayList<>(tokens);
        // Remove start token
        if (expressionTokens.get(0).tokenType != TokenType.START) {
            throw new IllegalStateException("First token must be a start token, current first token: " + expressionTokens.get(0));
        }
        expressionTokens.remove(0);
        try {
            Exp tree = expression(0, expressionTokens);

            if (expressionTokens.isEmpty()) {
                throw new IllegalStateException("Invalid expression (missing end token).");
            }

            if (expressionTokens.get(0).tokenType != TokenType.END) {
                throw new IllegalStateException("Tree finished constructing while not all tokens were consumed.");
            }
            // Remove end token
            expressionTokens.remove(0);
            return tree;
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException("Invalid expression (missing end token).");
        }
    }

    /**
     * Recursive method for constructing an expression node.
     *
     * @param precedence defines the minimal "strength" of an operator required to be taken into consideration
     * @param tokens     list of remaining tokens
     * @return an expression node
     */
    private static Exp expression(int precedence, List<ExpressionToken> tokens) {
        Exp node = primary(tokens);
        while (isBinaryOperator(tokens.get(0)) && getPrecedence(tokens.get(0)) >= precedence) {
            ExpressionToken op = tokens.remove(0);
            int prec = getPrecedence(op);
            if (!isRightAssociative(op)) prec += 1;

            Exp rightNode = expression(prec, tokens);
            if (!OperatorUtils.operatorMapper.containsKey(op.tokenValue.toLowerCase())) {
                throw new IllegalArgumentException("This is not an operator: " + op.tokenValue);
            }
            node = new BinaryExp(OperatorUtils.operatorMapper.get(op.tokenValue.toLowerCase()), node, rightNode);
        }
        return node;
    }

    /**
     * Method for constructing an operand node.
     *
     * @param tokens list of remaining tokens
     * @return an operand node. It can be a constant value node, a null value node, a variable node or a unary
     * expression node (an expression having a unary operator applied).
     * @throws IllegalStateException    if the brackets aren't properly closed after the expression inside has been processed.
     * @throws IllegalArgumentException if illegal token is provided.
     */
    private static Exp primary(List<ExpressionToken> tokens) {
        if (isUnaryOperator(tokens.get(0))) {
            ExpressionToken op = tokens.remove(0);
            int prec = getPrecedence(op);
            Exp node = expression(prec, tokens);
            if (!OperatorUtils.operatorMapper.containsKey(op.tokenValue.toLowerCase())) {
                throw new IllegalArgumentException("This is not an operator: " + op.tokenValue);
            }
            return new UnaryExp(OperatorUtils.operatorMapper.get(op.tokenValue.toLowerCase()), node);
        } else if (tokens.get(0).tokenType == TokenType.LEFT_P) {
            tokens.remove(0);
            Exp node = expression(0, tokens);
            if (tokens.get(0).tokenType != TokenType.RIGHT_P) {
                throw new IllegalStateException("Parenthesis not properly closed, instead given: " + tokens.get(0));
            }
            tokens.remove(0);
            return node;
        } else {
            ExpressionToken tok = tokens.remove(0);
            if (tok.tokenType == TokenType.VAR) {
                VariableExp node = new FieldExp(tok.tokenValue, null);

                if (tokens.get(0).tokenType == TokenType.DOT || tokens.get(0).tokenType == TokenType.LEFT_IDX_P) {
                    VariableExp tempNode = node;
                    do {
                        VariableExp nextNode;
                        if (tokens.get(0).tokenType == TokenType.DOT) {
                            tokens.remove(0);
                            if (tokens.get(0).tokenType != TokenType.VAR) {
                                throw new IllegalArgumentException("Expected variable token, got: " + tokens.get(0));
                            }
                            nextNode = new FieldExp(tokens.remove(0).tokenValue, null);

                        } else {
                            tokens.remove(0);
                            Exp index = expression(0, tokens);
                            if (tokens.get(0).tokenType != TokenType.RIGHT_IDX_P) {
                                throw new IllegalStateException("Index parenthesis not closed, instead given: " + tokens.get(0));
                            }
                            tokens.remove(0);
                            nextNode = new IndexExp(index, null);
                        }
                        tempNode.next = nextNode;
                        tempNode = nextNode;
                    } while (tokens.get(0).tokenType == TokenType.DOT || tokens.get(0).tokenType == TokenType.LEFT_IDX_P);
                }
                return node;
            } else if (tok.tokenType == TokenType.STR_CONST) {
                return new StringExp(tok.tokenValue);
            } else if (tok.tokenType == TokenType.NUM_CONST) {
                try {
                    return new NumberExp(Integer.parseInt(tok.tokenValue));
                } catch (NumberFormatException e) {
                    return new NumberExp(Double.parseDouble(tok.tokenValue));
                }
            } else if (tok.tokenType == TokenType.BOOL_CONST) {
                return new BooleanExp(Boolean.valueOf(tok.tokenValue));
            } else if (tok.tokenType == TokenType.NULL) {
                return new NullExp();
            } else {
                throw new IllegalArgumentException("Token given is not an operand: " + tok);
            }
        }
    }

    /**
     * Method that determines the precedence of an operator. An operator with lower precedence will be handled later during
     * evaluation (the higher precedence of the operator, the bigger depth of the node created from such operator)
     *
     * @param op token containing an operator
     * @return precedence level
     * @throws IllegalArgumentException if illegal token is provided
     */
    private static int getPrecedence(ExpressionToken op) {
        if (op.tokenType == TokenType.LOG_OP) {
            if (op.tokenValue.equals("||") || op.tokenValue.equalsIgnoreCase("or")) return 0;
            else if (op.tokenValue.equals("&&") || op.tokenValue.equalsIgnoreCase("and")) return 1;
            else throw new IllegalArgumentException(op + " is not a valid logical operator");
        } else if (op.tokenType == TokenType.LOG_UN_OP) return 2;
        else if (op.tokenType == TokenType.EQ_OP) return 3;
        else if (op.tokenType == TokenType.ADD_OP) return 4;
        else if (op.tokenType == TokenType.UN_OP) return 5;
        else if (op.tokenType == TokenType.MUL_OP) return 6;
        else if (op.tokenType == TokenType.EXP_OP) return 7;
        throw new IllegalArgumentException(op + " is not an operator");
    }

    private static boolean isBinaryOperator(ExpressionToken tok) {
        return tok.tokenType == TokenType.LOG_OP ||
                tok.tokenType == TokenType.EQ_OP ||
                tok.tokenType == TokenType.ADD_OP ||
                tok.tokenType == TokenType.MUL_OP ||
                tok.tokenType == TokenType.EXP_OP;
    }

    private static boolean isUnaryOperator(ExpressionToken tok) {
        return tok.tokenType == TokenType.LOG_UN_OP ||
                tok.tokenType == TokenType.UN_OP;
    }

    private static boolean isRightAssociative(ExpressionToken op) {
        return op.tokenType == TokenType.EXP_OP;
    }
}
