package com.lw.expressioneval.evaluator;

import com.lw.expressioneval.evaluator.enums.OperatorType;

import java.util.Map;

public class OperatorUtils {
    public static Map<String, OperatorType> operatorMapper = Map.ofEntries(
            Map.entry("^", OperatorType.OPERATOR_POWER),
            Map.entry("*", OperatorType.OPERATOR_MULTIPLY),
            Map.entry("/", OperatorType.OPERATOR_DIVIDE),
            Map.entry("+", OperatorType.OPERATOR_PLUS),
            Map.entry("-", OperatorType.OPERATOR_MINUS),
            Map.entry("and", OperatorType.OPERATOR_AND),
            Map.entry("&&", OperatorType.OPERATOR_AND),
            Map.entry("or", OperatorType.OPERATOR_OR),
            Map.entry("||", OperatorType.OPERATOR_OR),
            Map.entry("!", OperatorType.OPERATOR_NOT),
            Map.entry("==", OperatorType.OPERATOR_EQUAL),
            Map.entry("!=", OperatorType.OPERATOR_NOT_EQUAL),
            Map.entry(">=", OperatorType.OPERATOR_GREATER_EQUAL),
            Map.entry(">", OperatorType.OPERATOR_GREATER),
            Map.entry("<=", OperatorType.OPERATOR_LESS_EQUAL),
            Map.entry("<", OperatorType.OPERATOR_LESS)
    );
}
