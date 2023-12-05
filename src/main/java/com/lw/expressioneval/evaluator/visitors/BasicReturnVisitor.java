package com.lw.expressioneval.evaluator.visitors;

import com.lw.expressioneval.evaluator.enums.ReturnType;

public class BasicReturnVisitor implements ReturnVisitor {
    private static boolean isVariable(ReturnType operandType) {
        return operandType == ReturnType.VARIABLE || operandType == ReturnType.NON_BOOLEAN;
    }

    @Override
    public ReturnType returnForLogicOperator(ReturnType... operands) {
        return ReturnType.BOOLEAN;
    }

    @Override
    public ReturnType returnForPlusOperator(ReturnType left, ReturnType right) {
        if (left == ReturnType.STRING || right == ReturnType.STRING)
            return ReturnType.STRING;
        else return returnForNumberOperator(left, right);
    }

    @Override
    public ReturnType returnForDoubleOperator(ReturnType left, ReturnType right) {
        return ReturnType.DOUBLE;
    }

    @Override
    public ReturnType returnForNumberOperator(ReturnType left, ReturnType right) {
        if (isVariable(left) || isVariable(right))
            return ReturnType.NON_BOOLEAN;
        else if (left == ReturnType.DOUBLE || right == ReturnType.DOUBLE)
            return ReturnType.DOUBLE;
        else return ReturnType.INTEGER;
    }

    @Override
    public ReturnType returnForUnaryNumberOperator(ReturnType operand) {
        return isVariable(operand) ? ReturnType.NON_BOOLEAN : operand;
    }
}
