package com.lw.expressioneval.evaluator;

import com.lw.expressioneval.evaluator.enums.ReturnType;
import com.lw.expressioneval.evaluator.enums.TokenType;
import com.lw.expressioneval.evaluator.expressions.Exp;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that contains a static method which splits a logical expression represented as a string into a list of
 * {@link ExpressionToken} objects which are then validated.
 */
public class ExpressionParser {
    private ExpressionParser() {
    }

    private static Map<TokenType, List<TokenType>> validPredecessors = Map.ofEntries(
            Map.entry(TokenType.START, Collections.emptyList()),
            Map.entry(TokenType.EXP_OP, List.of(TokenType.RIGHT_P, TokenType.RIGHT_IDX_P, TokenType.VAR, TokenType.NUM_CONST)),
            Map.entry(TokenType.MUL_OP, List.of(TokenType.RIGHT_P, TokenType.RIGHT_IDX_P, TokenType.VAR, TokenType.NUM_CONST)),
            Map.entry(TokenType.ADD_OP, List.of(TokenType.RIGHT_P, TokenType.RIGHT_IDX_P, TokenType.VAR, TokenType.NUM_CONST, TokenType.STR_CONST)),
            Map.entry(TokenType.EQ_OP, List.of(TokenType.RIGHT_P, TokenType.RIGHT_IDX_P, TokenType.VAR, TokenType.NUM_CONST, TokenType.STR_CONST, TokenType.BOOL_CONST, TokenType.NULL)),
            Map.entry(TokenType.LOG_OP, List.of(TokenType.RIGHT_P, TokenType.RIGHT_IDX_P, TokenType.VAR, TokenType.NUM_CONST, TokenType.STR_CONST, TokenType.BOOL_CONST, TokenType.NULL)),
            Map.entry(TokenType.LOG_UN_OP, List.of(TokenType.START, TokenType.EQ_OP, TokenType.LOG_OP, TokenType.LEFT_P)),
            Map.entry(TokenType.UN_OP, List.of(TokenType.START, TokenType.EQ_OP, TokenType.LOG_OP, TokenType.LEFT_P, TokenType.LEFT_IDX_P)),
            Map.entry(TokenType.LEFT_P, List.of(TokenType.START, TokenType.EQ_OP, TokenType.EXP_OP, TokenType.MUL_OP, TokenType.ADD_OP, TokenType.LOG_OP, TokenType.LOG_UN_OP, TokenType.UN_OP, TokenType.LEFT_P, TokenType.LEFT_IDX_P)),
            Map.entry(TokenType.RIGHT_P, List.of(TokenType.RIGHT_P, TokenType.RIGHT_IDX_P, TokenType.VAR, TokenType.NUM_CONST, TokenType.STR_CONST, TokenType.BOOL_CONST, TokenType.NULL)),
            Map.entry(TokenType.LEFT_IDX_P, List.of(TokenType.RIGHT_IDX_P, TokenType.VAR)),
            Map.entry(TokenType.RIGHT_IDX_P, List.of(TokenType.RIGHT_P, TokenType.VAR, TokenType.NUM_CONST)),
            Map.entry(TokenType.VAR, List.of(TokenType.START, TokenType.EQ_OP, TokenType.EXP_OP, TokenType.MUL_OP, TokenType.ADD_OP, TokenType.LOG_OP, TokenType.LOG_UN_OP, TokenType.UN_OP, TokenType.LEFT_P, TokenType.LEFT_IDX_P, TokenType.DOT)),
            Map.entry(TokenType.NUM_CONST, List.of(TokenType.START, TokenType.EQ_OP, TokenType.EXP_OP, TokenType.MUL_OP, TokenType.ADD_OP, TokenType.LOG_OP, TokenType.UN_OP, TokenType.LEFT_P, TokenType.LEFT_IDX_P)),
            Map.entry(TokenType.STR_CONST, List.of(TokenType.START, TokenType.EQ_OP, TokenType.ADD_OP, TokenType.LOG_OP, TokenType.LEFT_P)),
            Map.entry(TokenType.BOOL_CONST, List.of(TokenType.START, TokenType.EQ_OP, TokenType.LOG_OP, TokenType.LOG_UN_OP, TokenType.LEFT_P)),
            Map.entry(TokenType.DOT, List.of(TokenType.RIGHT_IDX_P, TokenType.VAR)),
            Map.entry(TokenType.NULL, List.of(TokenType.START, TokenType.EQ_OP, TokenType.LOG_OP, TokenType.LEFT_P)),
            Map.entry(TokenType.END, List.of(TokenType.RIGHT_P, TokenType.RIGHT_IDX_P, TokenType.VAR, TokenType.NUM_CONST, TokenType.STR_CONST, TokenType.BOOL_CONST, TokenType.NULL))
    );

    private static Map<Character, TokenType> charToSimpleTokens = Map.ofEntries(
            Map.entry('(', TokenType.LEFT_P),
            Map.entry(')', TokenType.RIGHT_P),
            Map.entry('[', TokenType.LEFT_IDX_P),
            Map.entry(']', TokenType.RIGHT_IDX_P),
            Map.entry('*', TokenType.MUL_OP),
            Map.entry('/', TokenType.MUL_OP),
            Map.entry('^', TokenType.EXP_OP),
            Map.entry('.', TokenType.DOT)
    );

    /**
     * Method that validates logical expression output. The output is considered valid if it's return value is boolean
     * or if it is dependent on the existing variables in the expression
     *
     * @param tree root of the syntax tree that needs to be validated
     * @return whether the expression is considered logical
     */
    private static boolean isExpressionLogical(Exp tree) {
        ReturnType result = tree.returns();
        return result == ReturnType.BOOLEAN || result == ReturnType.VARIABLE;
    }


    /**
     * Method for transforming a string containing a logical expression into a syntax tree structure.
     * Expression is first split into {@link ExpressionToken} tokens which are validated following basic syntax rules.
     * Syntax tree is constructed from tokens and another validation check is performed to see whether the expression is logical.
     *
     * @param expression a logical expression string
     * @return {@link Exp} object representing the root of the syntax tree.
     * @throws IllegalStateException    if during syntax tree validation brackets aren't properly handled
     * @throws IllegalArgumentException if expression does not satisfy all validation rules
     */
    public static Exp parseExpression(String expression) throws IllegalArgumentException, IllegalStateException {
        List<ExpressionToken> tokens = generateTokens(expression);
        validateTokens(tokens);
        Exp tree = ExpressionConstructor.constructTree(tokens);
        if (!isExpressionLogical(tree)) {
            throw new IllegalArgumentException("Expression cannot provide a boolean value.");
        }
        return tree;
    }

    /**
     * Method for validating a list of {@link ExpressionToken} tokens. Method checks whether all the tokens are preceded
     * by a valid token and whether all the parenthesis are properly opened and closed
     *
     * @param tokens a list of tokens to validate
     * @throws IllegalArgumentException if any validation rule isn't met
     */
    protected static void validateTokens(List<ExpressionToken> tokens) {
        validatePredecessors(tokens);
        validateParenthesis(tokens);
    }

    /**
     * Method which checks whether all the tokens are preceded by a valid token and whether the list contains proper beginning and end.
     *
     * @param tokens a list of tokens to validate
     * @throws IllegalArgumentException if a predecessor is illegal or proper first/last token is missing.
     */
    private static void validatePredecessors(List<ExpressionToken> tokens) {
        if (tokens.get(0).getTokenType() != TokenType.START) {
            throw new IllegalArgumentException("First token must be a start token, current first token: " + tokens.get(0));
        }

        if (tokens.get(tokens.size() - 1).getTokenType() != TokenType.END) {
            throw new IllegalArgumentException("Last token must be an end token, current last token: " + tokens.get(tokens.size() - 1));
        }

        for (int idx = 1; idx < tokens.size(); idx++) {
            ExpressionToken curToken = tokens.get(idx);
            if (!validPredecessors.get(curToken.getTokenType()).contains(tokens.get(idx - 1).getTokenType())) {
                throw new IllegalArgumentException("Token '" + tokens.get(idx - 1) + "' must not appear before token '" + curToken + "'");
            }
        }
    }

    /**
     * Method which checks whether all the parenthesis are properly opened and closed. This is done by tracking one type
     * of parenthesis and switching the tracking to the other when they are opened.
     *
     * @param tokens a list of tokens to validate
     * @throws IllegalArgumentException if the parenthesis are closed before being opened or if not all parenthesis are closed
     */
    private static void validateParenthesis(List<ExpressionToken> tokens) {
        // logic for parenthesis counting
        boolean areBasicParenthesis = true;
        Stack<Integer> parenthesis = new Stack<>();
        int openParenthesisCount = 0;

        for (ExpressionToken token : tokens) {
            // check if brackets are properly opened and closed
            // this part counts number of open parenthesis of only one type at a time, switching when a different type appears
            if (token.getTokenType() == (areBasicParenthesis ? TokenType.LEFT_P : TokenType.LEFT_IDX_P)) {
                openParenthesisCount++;
            } else if (token.getTokenType() == (areBasicParenthesis ? TokenType.RIGHT_P : TokenType.RIGHT_IDX_P)) {
                if (openParenthesisCount == 0) {
                    throw new IllegalArgumentException("Parenthesis are closed before being opened.");
                }
                openParenthesisCount--;

                if (openParenthesisCount == 0 && !parenthesis.empty()) {
                    openParenthesisCount = parenthesis.pop();
                    areBasicParenthesis = !areBasicParenthesis;
                }
            } else if (token.getTokenType() == (areBasicParenthesis ? TokenType.LEFT_IDX_P : TokenType.LEFT_P)) {
                parenthesis.push(openParenthesisCount);
                openParenthesisCount = 1;
                areBasicParenthesis = !areBasicParenthesis;
            } else if (token.getTokenType() == (areBasicParenthesis ? TokenType.RIGHT_IDX_P : TokenType.RIGHT_P)) {
                throw new IllegalArgumentException("Parenthesis are closed before being opened.");
            }
        }

        if (openParenthesisCount != 0 || !parenthesis.empty()) {
            throw new IllegalArgumentException("Not all parenthesis have been closed.");
        }
    }

    /**
     * Method for creating tokens out of the logical expression string.
     *
     * @param expr a logical expression
     * @return list of tokens
     * @throws IllegalArgumentException if certain ordering of characters in a string is not allowed
     */
    protected static List<ExpressionToken> generateTokens(String expr) {
        List<ExpressionToken> tokens = new ArrayList<>();
        tokens.add(new ExpressionToken("", TokenType.START));

        String[] expression_sections = expr.split("\\s+");

        for (String section : expression_sections) {
            generateTokensFromSection(section, tokens);
        }

        // we successfully reached the end
        tokens.add(new ExpressionToken("", TokenType.END));
        return tokens;
    }

    private static void generateTokensFromSection(String section, List<ExpressionToken> tokens) {
        int idx = 0;
        while (idx < section.length()) {
            char c = section.charAt(idx);
            int consumeNext = 1;

            // some characters do not require additional parsing, so their processing is simple
            if (charToSimpleTokens.containsKey(c)) {
                tokens.add(new ExpressionToken(String.valueOf(c), charToSimpleTokens.get(c)));
            } else if (c == '+' || c == '-') {
                // + and - can be both unary and binary operators
                ExpressionToken previousToken = tokens.get(tokens.size() - 1);

                if (previousToken.getTokenType() == TokenType.RIGHT_P || previousToken.getTokenType() == TokenType.VAR ||
                        previousToken.getTokenType() == TokenType.NUM_CONST || previousToken.getTokenType() == TokenType.STR_CONST) {
                    tokens.add(new ExpressionToken(String.valueOf(c), TokenType.ADD_OP));
                } else {
                    tokens.add(new ExpressionToken(String.valueOf(c), TokenType.UN_OP));
                }
            } else if (Character.isLetterOrDigit(c) || c == '_' || c == '$') {
                // this regex can produce a number, null, boolean, logical operand or a variable
                String extractedString = findRegex(section.substring(idx), "[\\w$]+(\\.[\\w$]+)*");
                consumeNext = extractedString.length();

                try {
                    Double.parseDouble(extractedString);
                    tokens.add(new ExpressionToken(extractedString, TokenType.NUM_CONST));
                } catch (NumberFormatException e) {
                    if (extractedString.equalsIgnoreCase("or") || extractedString.equalsIgnoreCase("and") ||
                            extractedString.equalsIgnoreCase("not")) {
                        tokens.add(new ExpressionToken(extractedString, TokenType.LOG_OP));
                    } else if (extractedString.equalsIgnoreCase("null")) {
                        tokens.add(new ExpressionToken(extractedString, TokenType.NULL));
                    } else if (extractedString.equalsIgnoreCase("true") || extractedString.equalsIgnoreCase("false")) {
                        tokens.add(new ExpressionToken(extractedString, TokenType.BOOL_CONST));
                    } else {
                        String[] parts = extractedString.split("\\.");
                        tokens.add(new ExpressionToken(parts[0], TokenType.VAR));
                        for (int i = 1; i < parts.length; i++) {
                            tokens.add(new ExpressionToken(".", TokenType.DOT));
                            tokens.add(new ExpressionToken(parts[i], TokenType.VAR));
                        }
                    }
                }
            } else if (c == '"') {
                // quotation marks represent string literal
                String extractedString = findRegex(section.substring(idx), "\".*[^\\ ]\"");
                consumeNext = extractedString.length();

                // remove quotation marks as they are not part of the string literal
                tokens.add(new ExpressionToken(extractedString.substring(1, extractedString.length() - 1), TokenType.STR_CONST));
            } else {
                // last options are 2 character tokens or the ones depending on the following token
                char nextChar = idx == section.length() - 1 ? '\0' : section.charAt(idx + 1);
                if (c == '|' && nextChar == '|' || c == '&' && nextChar == '&') {
                    consumeNext = 2;
                    tokens.add(new ExpressionToken(section.substring(idx, idx + 2), TokenType.LOG_OP));
                } else if ((c == '=' || c == '!' || c == '<' || c == '>') && nextChar == '=') {
                    consumeNext = 2;
                    tokens.add(new ExpressionToken(section.substring(idx, idx + 2), TokenType.EQ_OP));
                } else if ((c == '<' || c == '>')) {
                    tokens.add(new ExpressionToken(String.valueOf(c), TokenType.EQ_OP));
                } else if (c == '!') {
                    tokens.add(new ExpressionToken(String.valueOf(c), TokenType.LOG_UN_OP));
                } else {
                    String msg = "Invalid character starting at index " + (idx) + " in section: " + section +
                            "\nCharacter '" + c + "' does not form any viable token.";
                    throw new IllegalArgumentException(msg);
                }
            }

            idx += consumeNext;
        }
    }

    /**
     * Method for extracting a substring that matches given pattern.
     *
     * @param text  string in which the search is performed
     * @param regex pattern that needs to be matched
     * @return substring that matches the given pattern
     * @throws IllegalArgumentException if the pattern cannot be found
     */
    private static String findRegex(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        } else {
            String msg = "Unable to find " + regex + "\nText: " + text;
            throw new IllegalArgumentException(msg);
        }
    }
}
