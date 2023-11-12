package com.lw.expressioneval.evaluator.expressions;

import java.util.Map;

/**
 * An abstract class that represents segment of a variable
 * e.g. an expression 'person[2].age < 28' has 3 variable segments: two fields 'person' and 'age' and an index '[2]'
 */
public abstract class VariableExp extends Exp {
    public VariableExp next;

    public abstract Object calculate(Map<String, Object> jsonObj, Object currentObj);
}
