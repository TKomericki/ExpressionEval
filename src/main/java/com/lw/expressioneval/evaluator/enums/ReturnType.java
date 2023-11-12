package com.lw.expressioneval.evaluator.enums;

/**
 * Represents types of return values of each node
 * NON_BOOLEAN means that the return type cannot be determined because one of the operand is a variable, however
 * the operation performed on the expression guarantees some sort of non-boolean result
 */
public enum ReturnType {
    INTEGER,
    DOUBLE,
    STRING,
    BOOLEAN,
    VARIABLE,
    NON_BOOLEAN,
    NULL
}
