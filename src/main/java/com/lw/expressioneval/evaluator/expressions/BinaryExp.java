package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.OperatorType;
import com.lw.expressioneval.evaluator.enums.ReturnType;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A class that represents a binary operation node
 */
@Getter
public class BinaryExp extends Exp {
    private final OperatorType operator;
    private final Exp left;
    private final Exp right;

    private static final List<OperatorType> validOperators = List.of(
            OperatorType.OPERATOR_PLUS,
            OperatorType.OPERATOR_MINUS,
            OperatorType.OPERATOR_MULTIPLY,
            OperatorType.OPERATOR_DIVIDE,
            OperatorType.OPERATOR_POWER,
            OperatorType.OPERATOR_AND,
            OperatorType.OPERATOR_OR,
            OperatorType.OPERATOR_EQUAL,
            OperatorType.OPERATOR_NOT_EQUAL,
            OperatorType.OPERATOR_GREATER,
            OperatorType.OPERATOR_GREATER_EQUAL,
            OperatorType.OPERATOR_LESS,
            OperatorType.OPERATOR_LESS_EQUAL
    );

    public BinaryExp(OperatorType o, Exp l, Exp r) {
        if (!validOperators.contains(o)) {
            throw new IllegalArgumentException("Unknown binary operator: " + o);
        }
        operator = o;
        left = l;
        right = r;

        checkReturnTypeValidity();
    }

    private void checkReturnTypeValidity() {
        ReturnType leftExpType = left.returns();
        ReturnType rightExpType = right.returns();

        boolean isLeftVar = leftExpType == ReturnType.VARIABLE || leftExpType == ReturnType.NON_BOOLEAN;
        boolean isRightVar = rightExpType == ReturnType.VARIABLE || rightExpType == ReturnType.NON_BOOLEAN;

        boolean cantBeNumber = !(leftExpType == ReturnType.INTEGER || leftExpType == ReturnType.DOUBLE || isLeftVar) ||
                !(rightExpType == ReturnType.INTEGER || rightExpType == ReturnType.DOUBLE || isRightVar);

        switch (operator) {
            case OPERATOR_MINUS, OPERATOR_MULTIPLY, OPERATOR_POWER, OPERATOR_GREATER, OPERATOR_LESS, OPERATOR_GREATER_EQUAL, OPERATOR_LESS_EQUAL -> {
                if (cantBeNumber) {
                    throw new IllegalArgumentException("Unable to apply '" + operator.label + "' binary operator on:\n" + left + "\n" + right);
                }
            }
            case OPERATOR_DIVIDE -> {
                if (cantBeNumber) {
                    throw new IllegalArgumentException("Unable to apply '" + operator.label + "' binary operator on:\n" + left + "\n" + right);
                } else if (rightExpType != ReturnType.VARIABLE && ((Number) right.calculate(null)).doubleValue() == 0) {
                    throw new IllegalArgumentException("Second operand of division is 0.");
                }
            }
            case OPERATOR_PLUS -> {
                if (!(leftExpType == ReturnType.INTEGER || leftExpType == ReturnType.DOUBLE || isLeftVar || leftExpType == ReturnType.STRING) ||
                        !(rightExpType == ReturnType.INTEGER || rightExpType == ReturnType.DOUBLE || isRightVar || rightExpType == ReturnType.STRING)) {
                    throw new IllegalArgumentException("Unable to apply '+' binary operator on:\n" + left + "\n" + right);
                }
            }
            case OPERATOR_AND, OPERATOR_OR -> {
                if (!(leftExpType == ReturnType.BOOLEAN || leftExpType == ReturnType.VARIABLE) ||
                        !(rightExpType == ReturnType.BOOLEAN || rightExpType == ReturnType.VARIABLE)) {
                    throw new IllegalArgumentException("Unable to apply '" + operator.label + "' binary operator on:\n" + left + "\n" + right);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator.label + " " + right + ")";
    }

    @Override
    public Object calculate(Map<String, Object> jsonObj) {
        Object l = left.calculate(jsonObj);

        // AND operator can finish without calculating right operand
        if (operator == OperatorType.OPERATOR_AND) {
            if (!(l instanceof Boolean)) {
                throw new IllegalArgumentException("Unable to apply '&&' binary operand.\nFirst operand is not a boolean:\n" + l);
            }
            if (!(Boolean) l) return false;
        }

        // OR operator can finish without calculating right operand
        if (operator == OperatorType.OPERATOR_OR) {
            if (!(l instanceof Boolean)) {
                throw new IllegalArgumentException("Unable to apply '||' binary operand.\nFirst operand is not a boolean:\n" + l);
            }
            if ((Boolean) l) return true;
        }

        Object r = right.calculate(jsonObj);

        switch (operator) {
            case OPERATOR_MINUS -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException("Unable to apply '-' binary operand.\nOne of the operands is not a number:\n" + l + "\n" + r);
                }
                return ((Number) l).doubleValue() - ((Number) r).doubleValue();
            }
            case OPERATOR_MULTIPLY -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException("Unable to apply '*' binary operand.\nOne of the operands is not a number:\n" + l + "\n" + r);
                }
                return ((Number) l).doubleValue() * ((Number) r).doubleValue();
            }
            case OPERATOR_DIVIDE -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException("Unable to apply '/' binary operand.\nOne of the operands is not a number:\n" + l + "\n" + r);
                }
                return ((Number) l).doubleValue() / ((Number) r).doubleValue();
            }
            case OPERATOR_PLUS -> {
                if (l instanceof String lString) {
                    return lString + r;
                } else if (r instanceof String rString) {
                    return l + rString;
                } else {
                    if (!(l instanceof Number && r instanceof Number)) {
                        throw new IllegalArgumentException("Unable to apply '+' binary operand.\nNone of the operators is a string and one of the operands is not a number:\n" + l + "\n" + r);
                    }
                }
                return ((Number) l).doubleValue() + ((Number) r).doubleValue();
            }
            case OPERATOR_POWER -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException("Unable to apply '^' binary operand.\nOne of the operands is not a number:\n" + l + "\n" + r);
                }
                return Math.pow(((Number) l).doubleValue(), ((Number) r).doubleValue());
            }
            case OPERATOR_OR -> {
                // first operand has already been checked so no need to repeat the check
                if (!(r instanceof Boolean)) {
                    throw new IllegalArgumentException("Unable to apply '&&' binary operand.\nSecond operand is not a boolean: " + r);
                }
                return (Boolean) l || (Boolean) r;
            }
            case OPERATOR_AND -> {
                if (!(r instanceof Boolean)) {
                    throw new IllegalArgumentException("Unable to apply '&&' binary operand.\nSecond operand is not a boolean: " + r);
                }
                return (Boolean) l && (Boolean) r;
            }
            case OPERATOR_EQUAL -> {
                if (l == null) {
                    return r == null;
                } else if (l instanceof Number lNumber && r instanceof Number rNumber) {
                    return Double.compare(lNumber.doubleValue(), rNumber.doubleValue()) == 0;
                } else {
                    return l.equals(r);
                }
            }
            case OPERATOR_NOT_EQUAL -> {
                if (l == null) {
                    return r != null;
                } else if (l instanceof Number lNumber && r instanceof Number rNumber) {
                    return !(Double.compare(lNumber.doubleValue(), rNumber.doubleValue()) == 0);
                } else {
                    return !l.equals(r);
                }
            }
            case OPERATOR_GREATER -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException("One of the operands is not a number:\n" + l + "\n" + r);
                }
                return ((Number) l).doubleValue() > ((Number) r).doubleValue();
            }
            case OPERATOR_LESS -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException("One of the operands is not a number:\n" + l + "\n" + r);
                }
                return ((Number) l).doubleValue() < ((Number) r).doubleValue();
            }
            case OPERATOR_GREATER_EQUAL -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException("One of the operands is not a number:\n" + l + "\n" + r);
                }
                return ((Number) l).doubleValue() >= ((Number) r).doubleValue();
            }
            case OPERATOR_LESS_EQUAL -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException("One of the operands is not a number:\n" + l + "\n" + r);
                }
                return ((Number) l).doubleValue() <= ((Number) r).doubleValue();
            }
            default -> throw new IllegalStateException("Unknown binary operator: " + operator);
        }
    }

    @Override
    public ReturnType returns() {
        ReturnType leftExpType = left.returns();
        ReturnType rightExpType = right.returns();

        boolean isVariable = leftExpType == ReturnType.VARIABLE || rightExpType == ReturnType.VARIABLE ||
                leftExpType == ReturnType.NON_BOOLEAN || rightExpType == ReturnType.NON_BOOLEAN;

        switch (operator) {
            case OPERATOR_MINUS, OPERATOR_MULTIPLY, OPERATOR_POWER -> {
                if (isVariable)
                    return ReturnType.NON_BOOLEAN;
                else if (leftExpType == ReturnType.DOUBLE || rightExpType == ReturnType.DOUBLE)
                    return ReturnType.DOUBLE;
                else return ReturnType.INTEGER;
            }
            case OPERATOR_DIVIDE -> {
                return ReturnType.DOUBLE;
            }
            case OPERATOR_PLUS -> {
                if (leftExpType == ReturnType.STRING || rightExpType == ReturnType.STRING)
                    return ReturnType.STRING;
                else if (isVariable)
                    return ReturnType.NON_BOOLEAN;
                else if (leftExpType == ReturnType.DOUBLE || rightExpType == ReturnType.DOUBLE)
                    return ReturnType.DOUBLE;
                else return ReturnType.INTEGER;
            }
            case OPERATOR_AND, OPERATOR_OR, OPERATOR_GREATER, OPERATOR_LESS, OPERATOR_GREATER_EQUAL, OPERATOR_LESS_EQUAL, OPERATOR_EQUAL, OPERATOR_NOT_EQUAL -> {
                return ReturnType.BOOLEAN;
            }
            default -> throw new IllegalStateException("Unknown binary operator: " + operator);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryExp binaryExp = (BinaryExp) o;
        return operator == binaryExp.operator && left.equals(binaryExp.left) && right.equals(binaryExp.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, left, right);
    }
}
