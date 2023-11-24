package com.lw.expressioneval.evaluator;

import com.lw.expressioneval.evaluator.enums.OperatorType;

import java.util.Map;

public class OperatorUtils {
    private OperatorUtils() {
    }

    public static Map<String, OperatorType> binaryOperatorMapper = Map.ofEntries(
            Map.entry("^", OperatorType.OPERATOR_POWER),
            Map.entry("*", OperatorType.OPERATOR_MULTIPLY),
            Map.entry("/", OperatorType.OPERATOR_DIVIDE),
            Map.entry("+", OperatorType.OPERATOR_PLUS),
            Map.entry("-", OperatorType.OPERATOR_MINUS),
            Map.entry("and", OperatorType.OPERATOR_AND),
            Map.entry("&&", OperatorType.OPERATOR_AND),
            Map.entry("or", OperatorType.OPERATOR_OR),
            Map.entry("||", OperatorType.OPERATOR_OR),
            Map.entry("==", OperatorType.OPERATOR_EQUAL),
            Map.entry("!=", OperatorType.OPERATOR_NOT_EQUAL),
            Map.entry(">=", OperatorType.OPERATOR_GREATER_EQUAL),
            Map.entry(">", OperatorType.OPERATOR_GREATER),
            Map.entry("<=", OperatorType.OPERATOR_LESS_EQUAL),
            Map.entry("<", OperatorType.OPERATOR_LESS)
    );

    public static Map<String, OperatorType> unaryOperatorMapper = Map.ofEntries(
            Map.entry("+", OperatorType.OPERATOR_UNARY_PLUS),
            Map.entry("-", OperatorType.OPERATOR_UNARY_MINUS),
            Map.entry("!", OperatorType.OPERATOR_NOT)
    );
}
