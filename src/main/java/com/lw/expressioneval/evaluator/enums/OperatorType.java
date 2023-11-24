package com.lw.expressioneval.evaluator.enums;

import com.lw.expressioneval.evaluator.visitors.ValidityVisitor;

/**
 * Represents categories of operators in syntax tree nodes
 */
public enum OperatorType {
    OPERATOR_PLUS("+") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForAdditionOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_UNARY_PLUS("+") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 1 && visitor.validateForUnaryNonLogicOperators(operands[0]);
        }
    },
    OPERATOR_MINUS("-") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }
    },
    OPERATOR_UNARY_MINUS("-") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 1 && visitor.validateForUnaryNonLogicOperators(operands[0]);
        }
    },
    OPERATOR_MULTIPLY("*") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }
    },
    OPERATOR_DIVIDE("/") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForDivisionOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_POWER("^") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }
    },
    OPERATOR_AND("&&") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForLogicOperators(operands[0], operands[1]);
        }
    },
    OPERATOR_OR("||") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForLogicOperators(operands[0], operands[1]);
        }
    },
    OPERATOR_NOT("!") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForUnaryLogicOperators(operands[0]);
        }
    },
    OPERATOR_EQUAL("==") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForEqualityOperators(operands[0], operands[1]);
        }
    },
    OPERATOR_NOT_EQUAL("!=") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForEqualityOperators(operands[0], operands[1]);
        }
    },
    OPERATOR_GREATER(">") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }
    },
    OPERATOR_GREATER_EQUAL(">=") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }
    },
    OPERATOR_LESS("<") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }
    },
    OPERATOR_LESS_EQUAL("<=") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }
    };

    private String label;

    public String getLabel() {
        return label;
    }

    abstract public boolean isValid(ValidityVisitor visitor, ReturnType... operands);

    OperatorType(String label) {
        this.label = label;
    }
}
