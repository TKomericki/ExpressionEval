package com.lw.expressioneval.evaluator.visitors;

import com.lw.expressioneval.evaluator.enums.ReturnType;

public interface ReturnVisitor {
    public ReturnType returnForLogicOperator(ReturnType... operands);

    public ReturnType returnForPlusOperator(ReturnType left, ReturnType right);

    public ReturnType returnForDoubleOperator(ReturnType left, ReturnType right);

    public ReturnType returnForNumberOperator(ReturnType left, ReturnType right);

    public ReturnType returnForUnaryNumberOperator(ReturnType operand);
}
