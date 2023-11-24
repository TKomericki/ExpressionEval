package com.lw.expressioneval.evaluator.visitors;

import com.lw.expressioneval.evaluator.enums.ReturnType;

public interface ValidityVisitor {
    public boolean validateForNumberOperators(ReturnType left, ReturnType right);
    public boolean validateForDivisionOperator(ReturnType left, ReturnType right);
    public boolean validateForAdditionOperator(ReturnType left, ReturnType right);
    public boolean validateForLogicOperators(ReturnType left, ReturnType right);
    public boolean validateForUnaryNonLogicOperators(ReturnType operand);
    public boolean validateForUnaryLogicOperators(ReturnType operand);
    public boolean validateForEqualityOperators(ReturnType left, ReturnType right);
}
