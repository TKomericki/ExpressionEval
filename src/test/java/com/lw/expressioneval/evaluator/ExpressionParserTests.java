package com.lw.expressioneval.evaluator;

import com.lw.expressioneval.evaluator.enums.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionParserTests {
    @Test
    void generateTokensSimpleSuccess() {
        String expression = ValidExpressions.validExpression1;
        assertEquals(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("variable", TokenType.VAR),
                new ExpressionToken(">", TokenType.EQ_OP),
                new ExpressionToken("10", TokenType.NUM_CONST),
                new ExpressionToken("", TokenType.END)
        ), ExpressionParser.generateTokens(expression));

        String literalExpression = "10 > 5 aND (true == true)";
        assertEquals(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("10", TokenType.NUM_CONST),
                new ExpressionToken(">", TokenType.EQ_OP),
                new ExpressionToken("5", TokenType.NUM_CONST),
                new ExpressionToken("aND", TokenType.LOG_OP),
                new ExpressionToken("(", TokenType.LEFT_P),
                new ExpressionToken("true", TokenType.BOOL_CONST),
                new ExpressionToken("==", TokenType.EQ_OP),
                new ExpressionToken("true", TokenType.BOOL_CONST),
                new ExpressionToken(")", TokenType.RIGHT_P),
                new ExpressionToken("", TokenType.END)
        ), ExpressionParser.generateTokens(literalExpression));
    }

    @Test
    void generateTokensVariableInputSuccess() {
        String expression = ValidExpressions.validExpression2;
        assertEquals(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("variable", TokenType.VAR),
                new ExpressionToken(".", TokenType.DOT),
                new ExpressionToken("_var", TokenType.VAR),
                new ExpressionToken(".", TokenType.DOT),
                new ExpressionToken("$name$", TokenType.VAR),
                new ExpressionToken("[", TokenType.LEFT_IDX_P),
                new ExpressionToken("1", TokenType.NUM_CONST),
                new ExpressionToken("]", TokenType.RIGHT_IDX_P),
                new ExpressionToken("[", TokenType.LEFT_IDX_P),
                new ExpressionToken("variable", TokenType.VAR),
                new ExpressionToken(".", TokenType.DOT),
                new ExpressionToken("3tokens", TokenType.VAR),
                new ExpressionToken("]", TokenType.RIGHT_IDX_P),
                new ExpressionToken(".", TokenType.DOT),
                new ExpressionToken("property", TokenType.VAR),
                new ExpressionToken("", TokenType.END)
        ), ExpressionParser.generateTokens(expression));
    }

    @Test
    void generateTokensComplexExpressionSuccess() {
        String expression = ValidExpressions.validExpression3;
        assertEquals(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("(", TokenType.LEFT_P),
                new ExpressionToken("customer", TokenType.VAR),
                new ExpressionToken(".", TokenType.DOT),
                new ExpressionToken("firstName", TokenType.VAR),
                new ExpressionToken("==", TokenType.EQ_OP),
                new ExpressionToken("JOHN", TokenType.STR_CONST),
                new ExpressionToken("&&", TokenType.LOG_OP),
                new ExpressionToken("customer", TokenType.VAR),
                new ExpressionToken(".", TokenType.DOT),
                new ExpressionToken("salary", TokenType.VAR),
                new ExpressionToken("<", TokenType.EQ_OP),
                new ExpressionToken("100", TokenType.NUM_CONST),
                new ExpressionToken(")", TokenType.RIGHT_P),
                new ExpressionToken("OR", TokenType.LOG_OP),
                new ExpressionToken("(", TokenType.LEFT_P),
                new ExpressionToken("customer", TokenType.VAR),
                new ExpressionToken(".", TokenType.DOT),
                new ExpressionToken("address", TokenType.VAR),
                new ExpressionToken("!=", TokenType.EQ_OP),
                new ExpressionToken("null", TokenType.NULL),
                new ExpressionToken("&&", TokenType.LOG_OP),
                new ExpressionToken("customer", TokenType.VAR),
                new ExpressionToken(".", TokenType.DOT),
                new ExpressionToken("address", TokenType.VAR),
                new ExpressionToken(".", TokenType.DOT),
                new ExpressionToken("city", TokenType.VAR),
                new ExpressionToken("==", TokenType.EQ_OP),
                new ExpressionToken("Washington", TokenType.STR_CONST),
                new ExpressionToken(")", TokenType.RIGHT_P),
                new ExpressionToken("", TokenType.END)
        ), ExpressionParser.generateTokens(expression));
    }

    @Test
    void generateTokensFailsWhenCharacterDoesNotFormAnyToken() {
        String expression = "var ? 10";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ExpressionParser.generateTokens(expression));
        assertEquals("Invalid character starting at index 0 in section: ?\nCharacter '?' does not form any viable token.", exception.getMessage());
    }

    @Test
    void validateTokensSuccess() {
        assertAll(
                () -> ExpressionParser.validateTokens(ExpressionParser.generateTokens(ValidExpressions.validExpression1)),
                () -> ExpressionParser.validateTokens(ExpressionParser.generateTokens(ValidExpressions.validExpression2)),
                () -> ExpressionParser.validateTokens(ExpressionParser.generateTokens(ValidExpressions.validExpression3))
        );
    }

    @Test
    void validateTokensFailsWhenIllegalTokenSuccessionOccurs() {
        String expression = "variable > < 10";
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ExpressionParser.validateTokens(ExpressionParser.generateTokens(expression)));
        assertEquals("Token '>' must not appear before token '<'", exception.getMessage());

        String expression2 = "5 ^ \"JOHN\" > john.salary";
        Exception exception2 = assertThrows(IllegalArgumentException.class,
                () -> ExpressionParser.validateTokens(ExpressionParser.generateTokens(expression2)));
        assertEquals("Token '^' must not appear before token 'JOHN'", exception2.getMessage());

        String expression3 = "variable[true] == null";
        Exception exception3 = assertThrows(IllegalArgumentException.class,
                () -> ExpressionParser.validateTokens(ExpressionParser.generateTokens(expression3)));
        assertEquals("Token '[' must not appear before token 'true'", exception3.getMessage());
    }

    @Test
    void validateTokensFailsWhenStartTokenIsMissing() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ExpressionParser.validateTokens(List.of(
                new ExpressionToken("variable", TokenType.VAR),
                new ExpressionToken(">", TokenType.EQ_OP),
                new ExpressionToken("10", TokenType.NUM_CONST),
                new ExpressionToken("", TokenType.END)
        )));
        assertEquals("First token must be a start token, current first token: variable", exception.getMessage());
    }

    @Test
    void validateTokensFailsWhenEndTokenIsMissing() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ExpressionParser.validateTokens(List.of(
                new ExpressionToken("", TokenType.START),
                new ExpressionToken("variable", TokenType.VAR),
                new ExpressionToken(">", TokenType.EQ_OP),
                new ExpressionToken("10", TokenType.NUM_CONST)
        )));
        assertEquals("Last token must be an end token, current last token: 10", exception.getMessage());
    }

    @Test
    void validateTokensFailsWhenParenthesisAreNotProperlyOpenedAndClosed() {
        String expression = "(variable > 10";
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ExpressionParser.validateTokens(ExpressionParser.generateTokens(expression)));
        assertEquals("Not all parenthesis have been closed.", exception.getMessage());

        String expression2 = "(person[2)].age > 10";
        Exception exception2 = assertThrows(IllegalArgumentException.class,
                () -> ExpressionParser.validateTokens(ExpressionParser.generateTokens(expression2)));
        assertEquals("Parenthesis are closed before being opened.", exception2.getMessage());

        String expression3 = "!(((variable > 10) == true))) or fALSe";
        Exception exception3 = assertThrows(IllegalArgumentException.class,
                () -> ExpressionParser.validateTokens(ExpressionParser.generateTokens(expression3)));
        assertEquals("Parenthesis are closed before being opened.", exception3.getMessage());
    }

    @Test
    void parseExpressionSuccess() {
        assertAll(() -> ExpressionParser.parseExpression(ValidExpressions.validExpression1),
                () -> ExpressionParser.parseExpression(ValidExpressions.validExpression2),
                () -> ExpressionParser.parseExpression(ValidExpressions.validExpression3));
    }

    @Test
    void parseExpressionFailsWhenResultCannotBeBoolean() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ExpressionParser.parseExpression("a^3 + 5"));
        assertEquals("Expression cannot provide a boolean value.", exception.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class,
                () -> ExpressionParser.parseExpression("\"JOHN\""));
        assertEquals("Expression cannot provide a boolean value.", exception2.getMessage());

        Exception exception3 = assertThrows(IllegalArgumentException.class,
                () -> ExpressionParser.parseExpression("a[0] * b * c"));
        assertEquals("Expression cannot provide a boolean value.", exception3.getMessage());
    }
}
