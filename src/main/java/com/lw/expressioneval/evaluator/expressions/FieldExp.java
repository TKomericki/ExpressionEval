package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.ReturnType;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

/**
 * A class that represents field name of a variable in the expression
 * e.g. in an expression 'person[2].age < 28' there are two instances of this class, one denoting 'person' and the
 * other denoting 'age'
 */
@Getter
public class FieldExp extends VariableExp {
    private String name;
    private VariableExp next;

    public FieldExp(String name, VariableExp next) {
        this.name = name;
        this.next = next;
    }

    @Override
    public String toString() {
        if (next == null) {
            return name;
        } else {
            return String.format("%s%s%s", name, next instanceof IndexExp ? "" : ".", next);
        }
    }

    @Override
    public Object calculate(Map<String, Object> jsonObj) {
        if (jsonObj == null) {
            throw new IllegalArgumentException("Provided object is null.");
        }

        Object result = jsonObj.get(name);
        return next == null ? result : next.calculate(jsonObj, result);
    }

    @Override
    public ReturnType returns() {
        if (next != null) {
            next.returns();
        }
        return ReturnType.VARIABLE;
    }

    @Override
    public Object calculate(Map<String, Object> jsonObj, Object currentObj) {
        if (!(currentObj instanceof Map)) {
            throw new IllegalArgumentException(String.format("Object given is not a map: %s", currentObj == null ? null : currentObj.getClass().getSimpleName()));
        }

        Object result = ((Map<?, ?>) currentObj).get(name);
        return next == null ? result : next.calculate(jsonObj, result);
    }

    public String getName() {
        return name;
    }

    @Override
    public VariableExp getNext() {
        return next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldExp fieldExp = (FieldExp) o;
        return name.equals(fieldExp.name) && ((next == null && fieldExp.next == null) || (next != null && next.equals(fieldExp.next)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, next);
    }
}
