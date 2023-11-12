package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.ReturnType;

import java.util.Map;
import java.util.Objects;

/**
 * A class that represents boolean node
 */
public class BooleanExp extends Exp {
    private final Boolean value;

    public BooleanExp(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public Object calculate(Map<String, Object> jsonObj) {
        return value;
    }

    @Override
    public ReturnType returns() {
        return ReturnType.BOOLEAN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanExp that = (BooleanExp) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
