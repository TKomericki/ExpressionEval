package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.OperatorType;
import com.lw.expressioneval.evaluator.enums.ReturnType;
import com.lw.expressioneval.evaluator.visitors.BasicCalculationVisitor;
import com.lw.expressioneval.evaluator.visitors.BasicValidityVisitor;
import com.lw.expressioneval.evaluator.visitors.ValidityVisitor;
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
            OperatorType.OPERATOR_UNARY_PLUS,
            OperatorType.OPERATOR_UNARY_MINUS,
            OperatorType.OPERATOR_NOT
    );

    public UnaryExp(OperatorType o, Exp e) {
        if (!validOperators.contains(o)) {
            throw new IllegalArgumentException(String.format("Unknown unary operator: %s", o));
        }
        operator = o;
        operand = e;

        checkReturnTypeValidity(new BasicValidityVisitor());
    }

    private void checkReturnTypeValidity(ValidityVisitor visitor) {
        ReturnType expType = operand.returns();
        if (!operator.isValid(visitor, expType)) {
            throw new IllegalArgumentException(String.format("Unable to apply '%s' unary operator on: %s", operator.getLabel(), operand));
        }
    }

    @Override
    public String toString() {
        return operator.getLabel() + operand;
    }

    @Override
    public Object calculate(Map<String, Object> jsonObj) {
        Object o = operand.calculate(jsonObj);
        try {
            return operator.calculate(new BasicCalculationVisitor(), o);
        } catch (ClassCastException | NullPointerException e) {
            throw new IllegalArgumentException(String.format("Unable to apply '%s' unary operator on: %s", operator.getLabel(), o));
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
