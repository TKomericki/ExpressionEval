package com.lw.expressioneval.mappers;

import com.lw.expressioneval.entities.ExpressionNode;
import com.lw.expressioneval.entities.ExpressionTree;
import com.lw.expressioneval.entities.NodeType;
import com.lw.expressioneval.evaluator.expressions.BooleanExp;
import com.lw.expressioneval.evaluator.expressions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that contains static methods for transforming expression syntax tree to database friendly format and vice versa
 */
public class ExpressionMapper {
    /**
     * A recursive method that transforms a syntax subtree into a list of all of its nodes in a format suitable for saving to database
     *
     * @param e syntax subtree
     * @return list of {@link ExpressionNode} objects representing all the nodes of the subtree
     */
    public static List<ExpressionNode> expressionToNodeList(Exp e) {
        List<ExpressionNode> nodes = new ArrayList<>();
        if (e instanceof BinaryExp) {
            ExpressionNode node = new ExpressionNode(NodeType.BINARY, null, null, null, ((BinaryExp) e).getOperator(), null);
            nodes.addAll(expressionToNodeList(((BinaryExp) e).getLeft()));
            node.setLeftOperand(nodes.get(nodes.size() - 1));
            nodes.addAll(expressionToNodeList(((BinaryExp) e).getRight()));
            node.setRightOperand(nodes.get(nodes.size() - 1));

            nodes.add(node);
        } else if (e instanceof UnaryExp) {
            ExpressionNode node = new ExpressionNode(NodeType.UNARY, null, null, null, ((UnaryExp) e).getOperator(), null);
            nodes.addAll(expressionToNodeList(((UnaryExp) e).getOperand()));
            node.setRightOperand(nodes.get(nodes.size() - 1));

            nodes.add(node);
        } else if (e instanceof FieldExp) {
            ExpressionNode node = new ExpressionNode(NodeType.FIELD, null, null, null, null, ((FieldExp) e).getName());

            // if there are more expressions in chain, generate them and set them as right operand
            if (((FieldExp) e).next != null) {
                nodes.addAll(expressionToNodeList(((FieldExp) e).next));
                node.setRightOperand(nodes.get(nodes.size() - 1));
            }
            nodes.add(node);

        } else if (e instanceof IndexExp) {
            // create index nodes first
            nodes.addAll(expressionToNodeList(((IndexExp) e).getIndex()));
            ExpressionNode node = new ExpressionNode(NodeType.INDEX, null, nodes.get(nodes.size() - 1), null, null, null);

            // if there are more fields or indexes in chain, store them as right node
            if (((IndexExp) e).next != null) {
                nodes.addAll(expressionToNodeList(((IndexExp) e).next));
                node.setRightOperand(nodes.get(nodes.size() - 1));
            }
            nodes.add(node);
        } else if (e instanceof NumberExp || e instanceof StringExp || e instanceof BooleanExp || e instanceof NullExp) {
            Object value = e.calculate(null);
            NodeType type;
            if (e instanceof NumberExp) type = NodeType.NUMBER;
            else if (e instanceof StringExp) type = NodeType.STRING;
            else if (e instanceof BooleanExp) type = NodeType.BOOLEAN;
            else type = NodeType.NULL;

            nodes.add(new ExpressionNode(type, null, null, null, null, value == null ? null : value.toString()));
        } else {
            throw new IllegalArgumentException("Encountered unchecked Exp subclass: " + e.getClass());
        }

        return nodes;
    }

    /**
     * A method that transforms a {@link ExpressionTree} object into a syntax tree
     *
     * @param tree {@link ExpressionTree} object
     * @return {@link Exp} object denoting syntax tree
     */
    public static Exp treeToExpression(ExpressionTree tree) {
        return nodeToExpression(tree.getRootNode());
    }

    /**
     * A recursive method that transforms a node (and all its children nodes) into an {@link Exp} object
     *
     * @param node {@link ExpressionNode} object
     * @return {@link Exp} object denoting syntax subtree
     * @throws IllegalStateException if node type is not known
     */
    private static Exp nodeToExpression(ExpressionNode node) {
        switch (node.getNodeType()) {
            case BINARY -> {
                Exp leftOperand = nodeToExpression(node.getLeftOperand());
                Exp rightOperand = nodeToExpression(node.getRightOperand());
                return new BinaryExp(node.getOperator(), leftOperand, rightOperand);
            }
            case UNARY -> {
                Exp operand = nodeToExpression(node.getRightOperand());
                return new UnaryExp(node.getOperator(), operand);
            }
            case FIELD -> {
                return new FieldExp(node.getNodeValue(), node.getRightOperand() == null ? null : (VariableExp) nodeToExpression(node.getRightOperand()));
            }
            case INDEX -> {
                Exp index = nodeToExpression(node.getLeftOperand());
                return new IndexExp(index, node.getRightOperand() == null ? null : (VariableExp) nodeToExpression(node.getRightOperand()));
            }
            case STRING -> {
                return new StringExp(node.getNodeValue());
            }
            case NUMBER -> {
                try {
                    return new NumberExp(Integer.parseInt(node.getNodeValue()));
                } catch (NumberFormatException e) {
                    return new NumberExp(Double.parseDouble(node.getNodeValue()));
                }
            }
            case BOOLEAN -> {
                return new BooleanExp(Boolean.valueOf(node.getNodeValue()));
            }
            case NULL -> {
                return new NullExp();
            }
            default -> throw new IllegalStateException("Encountered unchecked Node type, got: " + node.getNodeType());
        }
    }
}
