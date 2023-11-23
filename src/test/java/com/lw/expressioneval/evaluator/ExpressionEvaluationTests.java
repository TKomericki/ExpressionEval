package com.lw.expressioneval.evaluator;

import com.lw.expressioneval.evaluator.expressions.Exp;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpressionEvaluationTests {
    @Test
    void evaluateSuccess() {
        Exp ex = ExpressionParser.parseExpression(ValidExpressions.validExpression1);
        assertEquals(false, ex.calculate(Map.of("variable", 4)));
        assertEquals(true, ex.calculate(Map.of("variable", 30)));

        Exp ex2 = ExpressionParser.parseExpression(ValidExpressions.validExpression2);
        assertEquals(false, ex2.calculate(Map.of("variable", Map.of("_var", Map.of("$name$", List.of(Collections.emptyList(), List.of(Map.of("property", false)))), "3tokens", 0))));
        assertEquals(true, ex2.calculate(Map.of("variable", Map.of("_var", Map.of("$name$", List.of(Collections.emptyList(), List.of(Map.of("property", true)))), "3tokens", 0))));

        Exp ex3 = ExpressionParser.parseExpression(ValidExpressions.validExpression3);
        assertEquals(false, ex3.calculate(Map.of("customer", Map.of("firstName", "Michael", "salary", 95))));
        assertEquals(true, ex3.calculate(Map.of("customer", Map.of("firstName", "JOHN", "salary", 95, "address", Map.of("city", "Chicago")))));
    }

    @Test
    void evaluateFailsWhenMapIsNull() {
        Exp ex = ExpressionParser.parseExpression(ValidExpressions.validExpression1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ex.calculate(null));
        assertEquals("Provided object is null.", exception.getMessage());

        Exp ex2 = ExpressionParser.parseExpression(ValidExpressions.validExpression2);
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> ex2.calculate(Map.of("variable", "value")));
        assertEquals("Object given is not a map: String", exception2.getMessage());

        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> ex2.calculate(Map.of("wrong_variable", "value")));
        assertEquals("Object given is not a map: null", exception3.getMessage());
    }

    @Test
    void evaluateFailsWhenIndexIsInvalid() {
        Exp ex = ExpressionParser.parseExpression("person[idx].age < 28");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> ex.calculate(Map.of("person", 23.8, "idx", 0)));
        assertEquals("Unable to apply an index, the object is not an array: Double", exception.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> ex.calculate(Map.of("person", List.of(20, 14), "idx", -4)));
        assertEquals("Index must be a non-negative integer, but expression: idx\nReturns: -4", exception2.getMessage());

        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> ex.calculate(Map.of("person", List.of(20, 14), "idx", false)));
        assertEquals("Index must be an integer, but expression: idx\nReturns: Boolean", exception3.getMessage());

        Exception exception4 = assertThrows(IllegalArgumentException.class, () -> ex.calculate(Map.of("person", List.of(20, 14), "idx", 3)));
        assertEquals("Index out of bounds for the given list. Given index: 3\nList: [20, 14]", exception4.getMessage());
    }

    @Test
    void evaluateFailsWhenVariableTypesAndOperatorsMismatch() {
        Exp ex = ExpressionParser.parseExpression(ValidExpressions.validExpression1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ex.calculate(Map.of("variable", "Nick")));
        assertEquals("One of the operands is not a number:\nNick\n10", exception.getMessage());

        Exp ex2 = ExpressionParser.parseExpression("left and right");
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> ex2.calculate(Map.of("left", true, "right", 15)));
        assertEquals("Unable to apply '&&' binary operand.\nSecond operand is not a boolean: 15", exception2.getMessage());

        Exp ex3 = ExpressionParser.parseExpression("+variable > 20");
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> ex3.calculate(Map.of("variable", "Nick")));
        assertEquals("Unable to apply '+' unary operator on: Nick", exception3.getMessage());

    }
}
