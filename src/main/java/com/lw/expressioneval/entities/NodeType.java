package com.lw.expressioneval.entities;

/**
 * Represents possible categories of {@link ExpressionNode} object
 */
public enum NodeType {
    BINARY,
    UNARY,
    INDEX,
    FIELD,
    NUMBER,
    STRING,
    BOOLEAN,
    NULL,

}
