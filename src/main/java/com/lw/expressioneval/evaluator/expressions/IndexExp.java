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
    public final Exp index;

    public IndexExp(Exp index, VariableExp next) {
        this.index = index;
        this.next = next;
        checkReturnTypeValidity();
    }

    @Override
    public String toString() {
        if (next == null) {
            return "[" + index.toString() + "]";
        } else {
            return "[" + index.toString() + "]" + (next instanceof IndexExp ? "" : ".") + next;
        }
    }

    private void checkReturnTypeValidity() {
        ReturnType indexType = index.returns();
        if (!(indexType == ReturnType.INTEGER || indexType == ReturnType.VARIABLE || indexType == ReturnType.NON_BOOLEAN)) {
            throw new IllegalArgumentException("Index is neither integer nor a variable value, got: " + index);
        }
        if (indexType == ReturnType.INTEGER && ((Number) index.calculate(null)).intValue() < 0) {
            throw new IllegalArgumentException("Index must be a non-negative integer, got: " + index);
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
            throw new IllegalArgumentException("Unable to apply an index, the object is not an array: " + (currentObj == null ? null : currentObj.getClass().getSimpleName()));
        }

        Object key = index.calculate(jsonObj);

        if (!(key instanceof Integer)) {
            throw new IllegalArgumentException("Index must be an integer, but expression: " + index + "\nReturns: " + key.getClass().getSimpleName());
        } else if ((Integer) key < 0) {
            throw new IllegalArgumentException("Index must be a non-negative integer, but expression: " + index + "\nReturns: " + key);
        }

        try {
            Object result = ((List<?>) currentObj).get((Integer) key);
            return next == null ? result : next.calculate(jsonObj, result);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Index out of bounds for the given list. Given index: " + key + "\nList: " + currentObj);
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
