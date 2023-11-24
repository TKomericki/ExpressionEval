package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.ReturnType;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A class that represents index of a variable in the expression
 * e.g. in logical expression 'person[2].age < 28', '[2]' would be expressed with an instance of this class, having
 * a {@link NumberExp} object denoting '2' stored as its index, and {@link FieldExp} object denoting 'age' stored as
 * next expression
 */
@Getter
public class IndexExp extends VariableExp {
    private Exp index;
    private VariableExp next;

    public IndexExp(Exp index, VariableExp next) {
        if (index == null) {
            throw new IllegalArgumentException("An index expression must contain some index value");
        }
        this.index = index;
        this.next = next;
        checkReturnTypeValidity();
    }

    @Override
    public VariableExp getNext() {
        return next;
    }

    @Override
    public String toString() {
        if (next == null) {
            return String.format("[%s]", index);
        } else {
            return String.format("[%s]%s%s", index, next instanceof IndexExp ? "" : ".", next);
        }
    }

    private void checkReturnTypeValidity() {
        ReturnType indexType = index.returns();

        if (!(indexType == ReturnType.VARIABLE || indexType == ReturnType.NON_BOOLEAN)) {
            if (indexType != ReturnType.INTEGER) {
                throw new IllegalArgumentException(String.format("Index is neither integer nor a variable value, got: %s", index));
            } else if (((Number) index.calculate(null)).intValue() < 0) {
                throw new IllegalArgumentException(String.format("Index must be a non-negative integer, got: %s", index));
            }
        }

        if (next != null) {
            next.returns();
        }
    }

    @Override
    public Object calculate(Map<String, Object> jsonObj) {
        throw new IllegalStateException("Calling index before defining an object is illegal.");

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
        if (!(currentObj instanceof List<?>)) {
            throw new IllegalArgumentException(String.format("Unable to apply an index, the object is not an array: %s", currentObj == null ? null : currentObj.getClass().getSimpleName()));
        }

        Object key = index.calculate(jsonObj);

        if (!(key instanceof Integer)) {
            throw new IllegalArgumentException(String.format("Index must be an integer, but expression: %s\nReturns: %s", index, key.getClass().getSimpleName()));
        } else if ((Integer) key < 0) {
            throw new IllegalArgumentException(String.format("Index must be a non-negative integer, but expression: %s\nReturns: %s", index, key));
        }

        try {
            Object result = ((List<?>) currentObj).get((Integer) key);
            return next == null ? result : next.calculate(jsonObj, result);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(String.format("Index out of bounds for the given list. Given index: %s\nList: %s", key, currentObj));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexExp indexExp = (IndexExp) o;
        return index.equals(indexExp.index) && ((next == null && indexExp.next == null) || (next != null && next.equals(indexExp.next)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, next);
    }
}
