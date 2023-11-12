package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.ReturnType;

import java.util.Map;
import java.util.Objects;

/**
 * A class that represents number literal node
 */
public class NumberExp extends Exp {
    private final Number value;

    public NumberExp(Number value) {
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
        return value instanceof Integer ? ReturnType.INTEGER : ReturnType.DOUBLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberExp numberExp = (NumberExp) o;
        return value.equals(numberExp.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
