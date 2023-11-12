package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.ReturnType;

import java.util.Map;

/**
 * A class that represents null node
 */
public class NullExp extends Exp {
    public NullExp() {
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public Object calculate(Map<String, Object> jsonObj) {
        return null;
    }

    @Override
    public ReturnType returns() {
        return ReturnType.NULL;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o != null && getClass() == o.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
