package com.lw.expressioneval.evaluator.visitors;

public interface CalculationVisitor {
    public Object calculateAddition(Object left, Object right);

    public Object calculateSubtraction(Object left, Object right);

    public Object calculateMultiplication(Object left, Object right);

    public Object calculateDivision(Object left, Object right);

    public Object calculatePower(Object left, Object right);

    public Object calculateLogicAnd(Object left, Object right);

    public Object calculateLogicOr(Object left, Object right);

    public Object calculateEquality(Object left, Object right);

    public Object calculateInequality(Object left, Object right);

    public Object calculateGreaterThan(Object left, Object right);

    public Object calculateGreaterOrEqual(Object left, Object right);

    public Object calculateLessThan(Object left, Object right);

    public Object calculateLessOrEqual(Object left, Object right);

    public Object calculateUnaryPlus(Object operand);

    public Object calculateUnaryMinus(Object operand);

    public Object calculateLogicNot(Object operand);
}
