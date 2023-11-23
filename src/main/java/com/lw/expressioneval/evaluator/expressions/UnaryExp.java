package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.OperatorType;
import com.lw.expressioneval.evaluator.enums.ReturnType;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A class that represents a unary operation node
 */
@Getter
public class UnaryExp extends Exp {
    private OperatorType operator;
    private Exp operand;

    private static List<OperatorType> validOperators = List.of(
            OperatorType.OPERATOR_PLUS,
            OperatorType.OPERATOR_MINUS,
            OperatorType.OPERATOR_NOT
    );

    public UnaryExp(OperatorType o, Exp e) {
        if (!validOperators.contains(o)) {
            throw new IllegalArgumentException(String.format("Unknown unary operator: %s", o));
        }
        operator = o;
        operand = e;

        checkReturnTypeValidity();
    }

    private void checkReturnTypeValidity() {
        ReturnType expType = operand.returns();
        switch (operator) {
            case OPERATOR_PLUS, OPERATOR_MINUS -> {
                if (!(expType == ReturnType.INTEGER || expType == ReturnType.DOUBLE || expType == ReturnType.VARIABLE || expType == ReturnType.NON_BOOLEAN)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '%s' unary operator on: %s", operator.label, operand));
                }
            }
            case OPERATOR_NOT -> {
                if (!(expType == ReturnType.BOOLEAN || expType == ReturnType.VARIABLE)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '!' unary operator on: %s", operand));
                }
            }
        }
    }

    @Override
    public String toString() {
        return operator.label + operand;
    }

    @Override
    public Object calculate(Map<String, Object> jsonObj) {
        Object o = operand.calculate(jsonObj);
        switch (operator) {
            case OPERATOR_MINUS -> {
                if (!(o instanceof Number)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '-' unary operator on: %s", o));
                }
                return o instanceof Integer ? -((Integer) o) : -((Double) o);
            }
            case OPERATOR_PLUS -> {
                if (!(o instanceof Number)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '+' unary operator on: %s", o));
                }
                return o;
            }
            case OPERATOR_NOT -> {
                if (!(o instanceof Boolean)) {
                    throw new IllegalArgumentException(String.format("Unable to apply '!' unary operator on: %s", o));
                }
                return !(Boolean) o;
            }
            default -> throw new IllegalStateException(String.format("Unknown unary operator: %s", operator));
        }
    }

    @Override
    public ReturnType returns() {
        return operand.returns();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnaryExp unaryExp = (UnaryExp) o;
        return operator == unaryExp.operator && operand.equals(unaryExp.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, operand);
    }
}
