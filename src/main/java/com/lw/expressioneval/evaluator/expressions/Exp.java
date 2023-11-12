package com.lw.expressioneval.evaluator.expressions;

import com.lw.expressioneval.evaluator.enums.ReturnType;

import java.util.Map;

/**
 * An abstract class that represents syntax tree node
 */
public abstract class Exp {
    public abstract Object calculate(Map<String, Object> jsonObj);

    public abstract ReturnType returns();
}
