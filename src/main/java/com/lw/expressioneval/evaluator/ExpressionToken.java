package com.lw.expressioneval.evaluator;

import com.lw.expressioneval.evaluator.enums.TokenType;

import java.util.Objects;

/**
 * A class representing a token - a sequence of characters that are treated as a unit as it cannot be further broken down.
 */
public class ExpressionToken {
    private String tokenValue;
    private TokenType tokenType;

    public ExpressionToken(String tokenValue, TokenType tokenType) {
        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return tokenValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionToken that = (ExpressionToken) o;
        return tokenValue.equals(that.tokenValue) && tokenType == that.tokenType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenValue, tokenType);
    }
}
