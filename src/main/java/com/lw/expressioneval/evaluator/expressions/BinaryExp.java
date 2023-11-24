package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.OperatorType;
import com.lw.expressioneval.evaluator.enums.ReturnType;
import com.lw.expressioneval.evaluator.visitors.BasicValidityVisitor;
import com.lw.expressioneval.evaluator.visitors.ValidityVisitor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A class that represents a binary operation node
 */
@Getter
public class BinaryExp extends Exp {
    private OperatorType operator;
    private Exp left;
    private Exp right;

    private static List<OperatorType> validOperators = List.of(
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
            throw new IllegalArgumentException(String.format("Unknown binary operator: %s", o));
        }
        operator = o;
        left = l;
        right = r;

        checkReturnTypeValidity(new BasicValidityVisitor());

        // an additional check for division - if we know the second operand is 0, that is an illegal operation
        // TODO: consider leaving division by 0 a legal operation as Java can use infinity as value
        if (operator == OperatorType.OPERATOR_DIVIDE) {
            checkDivisionByZero();
        }
    }

    private void checkDivisionByZero() {
        ReturnType divisorType = right.returns();
        if ((divisorType == ReturnType.INTEGER || divisorType == ReturnType.DOUBLE) && ((Number) right.calculate(null)).doubleValue() == 0) {
            throw new IllegalArgumentException("Second operand of division is 0.");
        }
    }

    private void checkReturnTypeValidity(ValidityVisitor visitor) {
        ReturnType leftExpType = left.returns();
        ReturnType rightExpType = right.returns();

        if (!operator.isValid(visitor, leftExpType, rightExpType)) {
            throw new IllegalArgumentException(String.format("Unable to apply '%s' binary operator on:\n%s\n%s", operator.getLabel(), left, right));
        }
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, operator.getLabel(), right);
    }

    @Override
    public Object calculate(Map<String, Object> jsonObj) {
        Object l = left.calculate(jsonObj);

        // AND operator can finish without calculating right operand
        if (operator == OperatorType.OPERATOR_AND) {
            if (!(l instanceof Boolean)) {
                throw new IllegalArgumentException(String.format("Unable to apply '&&' binary operand.\nFirst operand is not a boolean:\n%s", l));
            }
            if (!(Boolean) l) return false;
        }

        // OR operator can finish without calculating right operand
        if (operator == OperatorType.OPERATOR_OR) {
            if (!(l instanceof Boolean)) {
                throw new IllegalArgumentException(String.format("Unable to apply '||' binary operand.\nFirst operand is not a boolean:\n%s", l));
            }
            if ((Boolean) l) return true;
        }

        Object r = right.calculate(jsonObj);

        switch (operator) {
            case OPERATOR_MINUS -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '-' binary operand.\nOne of the operands is not a number:\n%s\n%s", l, r));
                }
                return ((Number) l).doubleValue() - ((Number) r).doubleValue();
            }
            case OPERATOR_MULTIPLY -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '*' binary operand.\nOne of the operands is not a number:\n%s\n%s", l, r));
                }
                return ((Number) l).doubleValue() * ((Number) r).doubleValue();
            }
            case OPERATOR_DIVIDE -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '/' binary operand.\nOne of the operands is not a number:\n%s\n%s", l, r));
                }
                if (((Number) r).doubleValue() == 0) {
                    throw new IllegalArgumentException("Illegal division by 0.");
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
                        throw new IllegalArgumentException(String.format("Unable to apply '+' binary operand.\nNone of the operators is a string and one of the operands is not a number:\n%s\n%s", l, r));
                    }
                }
                return ((Number) l).doubleValue() + ((Number) r).doubleValue();
            }
            case OPERATOR_POWER -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '^' binary operand.\nOne of the operands is not a number:\n%s\n%s", l, r));
                }
                return Math.pow(((Number) l).doubleValue(), ((Number) r).doubleValue());
            }
            case OPERATOR_OR -> {
                // first operand has already been checked so no need to repeat the check
                if (!(r instanceof Boolean)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '&&' binary operand.\nSecond operand is not a boolean: %s", r));
                }
                return (Boolean) l || (Boolean) r;
            }
            case OPERATOR_AND -> {
                if (!(r instanceof Boolean)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '&&' binary operand.\nSecond operand is not a boolean: %s", r));
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
                    throw new IllegalArgumentException(String.format("One of the operands is not a number:\n%s\n%s", l, r));
                }
                return ((Number) l).doubleValue() > ((Number) r).doubleValue();
            }
            case OPERATOR_LESS -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException(String.format("One of the operands is not a number:\n%s\n%s", l, r));
                }
                return ((Number) l).doubleValue() < ((Number) r).doubleValue();
            }
            case OPERATOR_GREATER_EQUAL -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException(String.format("One of the operands is not a number:\n%s\n%s", l, r));
                }
                return ((Number) l).doubleValue() >= ((Number) r).doubleValue();
            }
            case OPERATOR_LESS_EQUAL -> {
                if (!(l instanceof Number && r instanceof Number)) {
                    throw new IllegalArgumentException(String.format("One of the operands is not a number:\n%s\n%s", l, r));
                }
                return ((Number) l).doubleValue() <= ((Number) r).doubleValue();
            }
            default -> throw new IllegalStateException(String.format("Unknown binary operator: %s", operator));
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
            default -> throw new IllegalStateException(String.format("Unknown binary operator: %s", operator));
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
