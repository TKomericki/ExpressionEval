package com.lw.expressioneval.evaluator;

import com.lw.expressioneval.evaluator.enums.TokenType;
import com.lw.expressioneval.evaluator.expressions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that constructs and validates expression syntax tree.
 */
public class ExpressionConstructor {
    private ExpressionConstructor() {
    }
    
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
        if (expressionTokens.get(0).getTokenType() != TokenType.START) {
            throw new IllegalStateException("First token must be a start token, current first token: " + expressionTokens.get(0));
        }
        expressionTokens.remove(0);

        try {
            Exp tree = expression(0, expressionTokens);

            // Remove end token
            if (expressionTokens.remove(0).getTokenType() != TokenType.END) {
                throw new IllegalStateException("Tree finished constructing while not all tokens were consumed.");
            }

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
            if (!OperatorUtils.operatorMapper.containsKey(op.getTokenValue().toLowerCase())) {
                throw new IllegalArgumentException("This is not an operator: " + op.getTokenValue());
            }

            int prec = getPrecedence(op);
            if (!isRightAssociative(op)) prec += 1;
            Exp rightNode = expression(prec, tokens);

            node = new BinaryExp(OperatorUtils.operatorMapper.get(op.getTokenValue().toLowerCase()), node, rightNode);
        }
        return node;
    }

    private static UnaryExp unary(List<ExpressionToken> tokens) {
        ExpressionToken op = tokens.remove(0);
        if (!OperatorUtils.operatorMapper.containsKey(op.getTokenValue().toLowerCase())) {
            throw new IllegalArgumentException("This is not an operator: " + op.getTokenValue());
        }

        int precedence = getPrecedence(op);
        Exp node = expression(precedence, tokens);
        return new UnaryExp(OperatorUtils.operatorMapper.get(op.getTokenValue().toLowerCase()), node);
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
            return unary(tokens);
        } else if (tokens.get(0).getTokenType() == TokenType.LEFT_P) {
            tokens.remove(0);
            Exp node = expression(0, tokens);
            if (tokens.remove(0).getTokenType() != TokenType.RIGHT_P) {
                throw new IllegalStateException("Parenthesis not properly closed, instead given: " + tokens.get(0));
            }
            return node;
        } else {
            ExpressionToken tok = tokens.remove(0);
            if (tok.getTokenType() == TokenType.VAR) {
                return new FieldExp(tok.getTokenValue(), getNextVariable(tokens));
            } else if (tok.getTokenType() == TokenType.STR_CONST) {
                return new StringExp(tok.getTokenValue());
            } else if (tok.getTokenType() == TokenType.NUM_CONST) {
                try {
                    return new NumberExp(Integer.parseInt(tok.getTokenValue()));
                } catch (NumberFormatException e) {
                    return new NumberExp(Double.parseDouble(tok.getTokenValue()));
                }
            } else if (tok.getTokenType() == TokenType.BOOL_CONST) {
                return new BooleanExp(Boolean.valueOf(tok.getTokenValue()));
            } else if (tok.getTokenType() == TokenType.NULL) {
                return new NullExp();
            } else {
                throw new IllegalArgumentException("Token given is not an operand: " + tok);
            }
        }
    }

    /**
     * A recursive method that creates a {@link VariableExp} object in the variable chain.
     * e.g. in an expression 'person[2].age' this method creates an {@link IndexExp} object with index value 2 and
     * recursively creates a {@link FieldExp} object with value 'age' as its successor, thus making a variable chain.
     * This method only handles successive parts of the variable chain, meaning that in a logical expression 'salary > 100'
     * this method should not be invoked.
     *
     * @param tokens list of remaining tokens
     * @return a variable node.
     * @throws IllegalStateException    if the dot token isn't followed by a variable token.
     * @throws IllegalArgumentException if index parenthesis have not been closed.
     */
    private static VariableExp getNextVariable(List<ExpressionToken> tokens) {
        if (tokens.get(0).getTokenType() == TokenType.DOT) {
            tokens.remove(0);
            if (tokens.get(0).getTokenType() != TokenType.VAR) {
                throw new IllegalArgumentException("Expected variable token, got: " + tokens.get(0));
            }
            String tokenValue = tokens.remove(0).getTokenValue();
            return new FieldExp(tokenValue, getNextVariable(tokens));
        } else if (tokens.get(0).getTokenType() == TokenType.LEFT_IDX_P) {
            tokens.remove(0);
            Exp index = expression(0, tokens);
            if (tokens.get(0).getTokenType() != TokenType.RIGHT_IDX_P) {
                throw new IllegalStateException("Index parenthesis not closed, instead given: " + tokens.get(0));
            }
            tokens.remove(0);
            return new IndexExp(index, getNextVariable(tokens));
        } else {
            return null;
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
        if (op.getTokenType() == TokenType.LOG_OP) {
            if (op.getTokenValue().equals("||") || op.getTokenValue().equalsIgnoreCase("or")) return 0;
            else if (op.getTokenValue().equals("&&") || op.getTokenValue().equalsIgnoreCase("and")) return 1;
            else throw new IllegalArgumentException(op + " is not a valid logical operator");
        } else if (op.getTokenType() == TokenType.LOG_UN_OP) return 2;
        else if (op.getTokenType() == TokenType.EQ_OP) return 3;
        else if (op.getTokenType() == TokenType.ADD_OP) return 4;
        else if (op.getTokenType() == TokenType.UN_OP) return 5;
        else if (op.getTokenType() == TokenType.MUL_OP) return 6;
        else if (op.getTokenType() == TokenType.EXP_OP) return 7;
        throw new IllegalArgumentException(op + " is not an operator");
    }

    private static boolean isBinaryOperator(ExpressionToken tok) {
        return tok.getTokenType() == TokenType.LOG_OP ||
                tok.getTokenType() == TokenType.EQ_OP ||
                tok.getTokenType() == TokenType.ADD_OP ||
                tok.getTokenType() == TokenType.MUL_OP ||
                tok.getTokenType() == TokenType.EXP_OP;
    }

    private static boolean isUnaryOperator(ExpressionToken tok) {
        return tok.getTokenType() == TokenType.LOG_UN_OP ||
                tok.getTokenType() == TokenType.UN_OP;
    }

    private static boolean isRightAssociative(ExpressionToken op) {
        return op.getTokenType() == TokenType.EXP_OP;
    }
}
