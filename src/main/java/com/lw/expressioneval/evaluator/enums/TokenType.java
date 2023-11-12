package com.lw.expressioneval.evaluator.enums;

/**
 * Represents possible categories of {@link com.lw.expressioneval.evaluator.ExpressionToken} object
 */
public enum TokenType {
    START,
    EXP_OP,
    MUL_OP,
    ADD_OP,
    EQ_OP,
    LOG_OP,
    UN_OP,
    LOG_UN_OP,
    LEFT_P,
    RIGHT_P,
    LEFT_IDX_P,
    RIGHT_IDX_P,
    VAR,
    DOT,
    NUM_CONST,
    STR_CONST,
    BOOL_CONST,
    NULL,
    END
}

