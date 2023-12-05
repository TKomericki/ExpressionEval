package com.lw.expressioneval.evaluator.visitors;

public class BasicCalculationVisitor implements CalculationVisitor {
    @Override
    public Object calculateAddition(Object left, Object right) {
        if (left == null || right == null) {
            throw new NullPointerException("One of the operators is null");
        }
        if (left instanceof String || right instanceof String) {
            return left.toString() + right.toString();
        } else if (left instanceof Double || right instanceof Double) {
            return ((Number) left).doubleValue() + ((Number) right).doubleValue();
        }
        return ((Number) left).intValue() + ((Number) right).intValue();
    }

    @Override
    public Object calculateSubtraction(Object left, Object right) {
        if (left instanceof Double || right instanceof Double) {
            return ((Number) left).doubleValue() - ((Number) right).doubleValue();
        }
        return ((Number) left).intValue() - ((Number) right).intValue();
    }

    @Override
    public Object calculateMultiplication(Object left, Object right) {
        if (left instanceof Double || right instanceof Double) {
            return ((Number) left).doubleValue() * ((Number) right).doubleValue();
        }
        return ((Number) left).intValue() * ((Number) right).intValue();
    }

    @Override
    public Object calculateDivision(Object left, Object right) {
        return ((Number) left).doubleValue() / ((Number) right).doubleValue();
    }

    @Override
    public Object calculatePower(Object left, Object right) {
        return Math.pow(((Number) left).doubleValue(), ((Number) right).doubleValue());
    }

    @Override
    public Object calculateLogicAnd(Object left, Object right) {
        return (Boolean) left && (Boolean) right;
    }

    @Override
    public Object calculateLogicOr(Object left, Object right) {
        return (Boolean) left || (Boolean) right;
    }

    @Override
    public Object calculateEquality(Object left, Object right) {
        if (left == null) {
            return right == null;
        } else if (left instanceof Number lNumber && right instanceof Number rNumber) {
            return Double.compare(lNumber.doubleValue(), rNumber.doubleValue()) == 0;
        } else {
            return left.equals(right);
        }
    }

    @Override
    public Object calculateInequality(Object left, Object right) {
        return !((Boolean) calculateEquality(left, right));
    }

    @Override
    public Object calculateGreaterThan(Object left, Object right) {
        return ((Number) left).doubleValue() > ((Number) right).doubleValue();
    }

    @Override
    public Object calculateGreaterOrEqual(Object left, Object right) {
        return ((Number) left).doubleValue() >= ((Number) right).doubleValue();
    }

    @Override
    public Object calculateLessThan(Object left, Object right) {
        return ((Number) left).doubleValue() < ((Number) right).doubleValue();
    }

    @Override
    public Object calculateLessOrEqual(Object left, Object right) {
        return ((Number) left).doubleValue() <= ((Number) right).doubleValue();
    }

    @Override
    public Object calculateUnaryPlus(Object operand) {
        return operand instanceof Double ? (Double) operand : +((Integer) operand);
    }

    @Override
    public Object calculateUnaryMinus(Object operand) {
        return operand instanceof Double ? ((Double) operand) : -((Integer) operand);
    }

    @Override
    public Object calculateLogicNot(Object operand) {
        return !((Boolean) operand);
    }
}
