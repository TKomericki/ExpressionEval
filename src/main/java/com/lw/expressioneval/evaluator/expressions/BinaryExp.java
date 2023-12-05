package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.OperatorType;
import com.lw.expressioneval.evaluator.enums.ReturnType;
import com.lw.expressioneval.evaluator.visitors.BasicCalculationVisitor;
import com.lw.expressioneval.evaluator.visitors.BasicReturnVisitor;
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
        Object r = null;

        try {
            // logical binary operators may be calculated without the right operator
            if (operator == OperatorType.OPERATOR_OR || operator == OperatorType.OPERATOR_AND) {
                Object result = operator.calculate(new BasicCalculationVisitor(), l, operator == OperatorType.OPERATOR_AND);
                if ((Boolean) result == (operator == OperatorType.OPERATOR_OR)) {
                    return result;
                }
            }

            // else, we need a second operator
            r = right.calculate(jsonObj);
            return operator.calculate(new BasicCalculationVisitor(), l, r);
        } catch (ClassCastException | NullPointerException e) {
            throw new IllegalArgumentException(String.format("Unable to apply '%s' binary operator on the following operands.\n%s\n%s", operator.getLabel(), l, r));
        }
    }

    @Override
    public ReturnType returns() {
        ReturnType leftExpType = left.returns();
        ReturnType rightExpType = right.returns();

        return operator.returns(new BasicReturnVisitor(), leftExpType, rightExpType);
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
