package com.lw.expressioneval.evaluator;

import com.lw.expressioneval.evaluator.enums.OperatorType;
import com.lw.expressioneval.evaluator.enums.TokenType;
import com.lw.expressioneval.evaluator.expressions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpressionConstructorTests {
    @Test
    void constructTreeSuccess() {
        Exp exp = ExpressionConstructor.constructTree(ExpressionParser.generateTokens(ValidExpressions.validExpression1));
        assertEquals(new BinaryExp(OperatorType.OPERATOR_GREATER, new FieldExp("variable", null), new NumberExp(10)), exp);

        Exp exp2 = ExpressionConstructor.constructTree(ExpressionParser.generateTokens(ValidExpressions.validExpression2));
        assertEquals(new FieldExp("variable",
                        new FieldExp("_var",
                                new FieldExp("$name$",
                                        new IndexExp(new NumberExp(1),
                                                new IndexExp(new FieldExp("variable",
                                                        new FieldExp("3tokens", null)),
                                                        new FieldExp("property", null)))))),
                exp2);

        Exp exp3 = ExpressionConstructor.constructTree(ExpressionParser.generateTokens(ValidExpressions.validExpression3));
        //constructing this large expression
        Exp leftBrackets = new BinaryExp(OperatorType.OPERATOR_AND,
                new BinaryExp(OperatorType.OPERATOR_EQUAL, new FieldExp("customer", new FieldExp("firstName", null)), new StringExp("JOHN")),
                new BinaryExp(OperatorType.OPERATOR_LESS, new FieldExp("customer", new FieldExp("salary", null)), new NumberExp(100)));
        Exp rightBrackets = new BinaryExp(OperatorType.OPERATOR_AND,
                new BinaryExp(OperatorType.OPERATOR_NOT_EQUAL, new FieldExp("customer", new FieldExp("address", null)), new NullExp()),
                new BinaryExp(OperatorType.OPERATOR_EQUAL, new FieldExp("customer", new FieldExp("address", new FieldExp("city", null))), new StringExp("Washington"))
        );
        assertEquals(new BinaryExp(OperatorType.OPERATOR_OR, leftBrackets, rightBrackets), exp3);
    }

    @Test
    void constructTreeFailsWhenThereIsNoStartToken() {
        Exception exception = assertThrows(IllegalStateException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("variable", TokenType.VAR),
                new ExpressionToken(">", TokenType.EQ_OP),
                new ExpressionToken("10", TokenType.NUM_CONST),
                new ExpressionToken("", TokenType.END)
        )));
        assertEquals("First token must be a start token, current first token: variable", exception.getMessage());
    }

    @Test
    void constructTreeFailsWhenThereIsNoEndToken() {
        Exception exception = assertThrows(IllegalStateException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("variable", TokenType.VAR),
                new ExpressionToken(">", TokenType.EQ_OP),
                new ExpressionToken("10", TokenType.NUM_CONST)
        )));
        assertEquals("Invalid expression (missing end token).", exception.getMessage());
    }

    @Test
    void constructTreeFailsWhenThereAreUnconsumedTokens() {
        // this can be done if list of tokens is invalid
        Exception exception = assertThrows(IllegalStateException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("variable", TokenType.VAR),
                new ExpressionToken(">", TokenType.EQ_OP),
                new ExpressionToken("10", TokenType.NUM_CONST),
                new ExpressionToken("10", TokenType.NUM_CONST)
        )));
        assertEquals("Tree finished constructing while not all tokens were consumed.", exception.getMessage());
    }

    @Test
    void constructTreeFailsWhenOperatorTokenHasInvalidStringRepresentation() {
        // this can be done if list of tokens is invalid
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("variable", TokenType.VAR),
                new ExpressionToken("+=", TokenType.EQ_OP),
                new ExpressionToken("10", TokenType.NUM_CONST),
                new ExpressionToken("", TokenType.END)
        )));
        assertEquals("This is not an operator: +=", exception.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("!!", TokenType.LOG_UN_OP),
                new ExpressionToken("true", TokenType.BOOL_CONST),
                new ExpressionToken("", TokenType.END)
        )));
        assertEquals("This is not an operator: !!", exception2.getMessage());
    }

    @Test
    void constructTreeFailsWhenDividingByZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("10.2", TokenType.NUM_CONST),
                new ExpressionToken("/", TokenType.MUL_OP),
                new ExpressionToken("0", TokenType.NUM_CONST),
                new ExpressionToken(">", TokenType.EQ_OP),
                new ExpressionToken("100", TokenType.NUM_CONST),
                new ExpressionToken("", TokenType.END)
        )));
        assertEquals("Second operand of division is 0.", exception.getMessage());
    }

    @Test
    void constructTreeFailsWhenOperandTypesAreInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("JOHN", TokenType.STR_CONST),
                new ExpressionToken("-", TokenType.ADD_OP),
                new ExpressionToken("10", TokenType.NUM_CONST),
                new ExpressionToken("", TokenType.END)
        )));
        assertEquals("Unable to apply '-' binary operator on:\n\"JOHN\"\n10", exception.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("JOHN", TokenType.STR_CONST),
                new ExpressionToken("Or", TokenType.LOG_OP),
                new ExpressionToken("salary", TokenType.VAR),
                new ExpressionToken("", TokenType.END)
        )));
        assertEquals("Unable to apply '||' binary operator on:\n\"JOHN\"\nsalary", exception2.getMessage());

        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("+", TokenType.UN_OP),
                new ExpressionToken("JOHN", TokenType.STR_CONST),
                new ExpressionToken("==", TokenType.EQ_OP),
                new ExpressionToken("salary", TokenType.VAR),
                new ExpressionToken("", TokenType.END)
        )));
        assertEquals("Unable to apply '+' unary operator on: \"JOHN\"", exception3.getMessage());
    }

    @Test
    void constructTreeFailsWhenIndexIsInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("salary", TokenType.VAR),
                new ExpressionToken("[", TokenType.LEFT_IDX_P),
                new ExpressionToken("-", TokenType.UN_OP),
                new ExpressionToken("1", TokenType.NUM_CONST),
                new ExpressionToken("]", TokenType.RIGHT_IDX_P),
                new ExpressionToken("==", TokenType.EQ_OP),
                new ExpressionToken("10", TokenType.NUM_CONST),
                new ExpressionToken("", TokenType.END)
        )));
        assertEquals("Index must be a non-negative integer, got: -1", exception.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> ExpressionConstructor.constructTree(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("salary", TokenType.VAR),
                new ExpressionToken("[", TokenType.LEFT_IDX_P),
                new ExpressionToken("false", TokenType.BOOL_CONST),
                new ExpressionToken("]", TokenType.RIGHT_IDX_P),
                new ExpressionToken("==", TokenType.EQ_OP),
                new ExpressionToken("10", TokenType.NUM_CONST),
                new ExpressionToken("", TokenType.END)
        )));
        assertEquals("Index is neither integer nor a variable value, got: false", exception2.getMessage());
    }
}
