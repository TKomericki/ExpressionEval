package com.lw.expressioneval.evaluator.visitors;

import com.lw.expressioneval.evaluator.enums.ReturnType;

import java.util.List;

public class BasicValidityVisitor implements ValidityVisitor {
    private static List<ReturnType> numberTypes = List.of(ReturnType.INTEGER, ReturnType.DOUBLE, ReturnType.VARIABLE, ReturnType.NON_BOOLEAN);
    @Override
    public boolean validateForNumberOperators(ReturnType left, ReturnType right) {
        return numberTypes.contains(left) && numberTypes.contains(right);
    }

    @Override
    public boolean validateForDivisionOperator(ReturnType left, ReturnType right) {
        return numberTypes.contains(left) && numberTypes.contains(right);
    }

    @Override
    public boolean validateForAdditionOperator(ReturnType left, ReturnType right) {
        return (numberTypes.contains(left) || left == ReturnType.STRING) && (numberTypes.contains(right) || right == ReturnType.STRING);
    }

    @Override
    public boolean validateForLogicOperators(ReturnType left, ReturnType right) {
        return (left == ReturnType.VARIABLE || left == ReturnType.BOOLEAN) && (right == ReturnType.VARIABLE || right == ReturnType.BOOLEAN);
    }

    @Override
    public boolean validateForUnaryNonLogicOperators(ReturnType operand) {
        return numberTypes.contains(operand);
    }

    @Override
    public boolean validateForUnaryLogicOperators(ReturnType operand) {
        return operand == ReturnType.VARIABLE || operand == ReturnType.BOOLEAN;
    }

    @Override
    public boolean validateForEqualityOperators(ReturnType left, ReturnType right) {
        return true;
    }
}
