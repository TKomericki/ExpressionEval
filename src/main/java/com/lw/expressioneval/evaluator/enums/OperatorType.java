package com.lw.expressioneval.evaluator.enums;

/**
 * Represents categories of operators in syntax tree nodes
 */
public enum OperatorType {
    OPERATOR_PLUS("+"),
    OPERATOR_MINUS("-"),
    OPERATOR_MULTIPLY("*"),
    OPERATOR_DIVIDE("/"),
    OPERATOR_POWER("^"),
    OPERATOR_AND("&&"),
    OPERATOR_OR("||"),
    OPERATOR_NOT("!"),
    OPERATOR_EQUAL("=="),
    OPERATOR_NOT_EQUAL("!="),
    OPERATOR_GREATER(">"),
    OPERATOR_GREATER_EQUAL(">="),
    OPERATOR_LESS("<"),
    OPERATOR_LESS_EQUAL("<=");

    public final String label;

    OperatorType(String label) {
        this.label = label;
    }
}
