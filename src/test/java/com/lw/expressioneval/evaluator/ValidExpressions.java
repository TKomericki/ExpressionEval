package com.lw.expressioneval.evaluator;

public class ValidExpressions {
    private ValidExpressions(){

    }
    public static final String validExpression1 = "variable > 10";
    public static final String validExpression2 = "variable._var.$name$[1][variable.3tokens].property";
    public static final String validExpression3 = "(customer.firstName == \"JOHN\" && customer.salary < 100) OR (customer.address != null && customer.address.city == \"Washington\")";
}
