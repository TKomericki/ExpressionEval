package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.ReturnType;

import java.util.Map;
import java.util.Objects;

/**
 * A class that represents string node
 */
public class StringExp extends Exp {
    private String value;

    public StringExp(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", value);
    }

    @Override
    public Object calculate(Map<String, Object> jsonObj) {
        return value;
    }

    @Override
    public ReturnType returns() {
        return ReturnType.STRING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringExp stringExp = (StringExp) o;
        return value.equals(stringExp.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
