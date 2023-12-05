package com.lw.expressioneval.evaluator.enums;

import com.lw.expressioneval.evaluator.visitors.CalculationVisitor;
import com.lw.expressioneval.evaluator.visitors.ReturnVisitor;
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

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateAddition(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForPlusOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_UNARY_PLUS("+") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 1 && visitor.validateForUnaryNonLogicOperators(operands[0]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 1) {
                throw new IllegalArgumentException(String.format("Required 1 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateUnaryPlus(operands[0]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 1) {
                throw new IllegalArgumentException(String.format("Required 1 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForUnaryNumberOperator(operands[0]);
        }
    },
    OPERATOR_MINUS("-") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateSubtraction(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForNumberOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_UNARY_MINUS("-") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 1 && visitor.validateForUnaryNonLogicOperators(operands[0]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 1) {
                throw new IllegalArgumentException(String.format("Required 1 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateUnaryMinus(operands[0]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 1) {
                throw new IllegalArgumentException(String.format("Required 1 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForUnaryNumberOperator(operands[0]);
        }
    },
    OPERATOR_MULTIPLY("*") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateMultiplication(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForNumberOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_DIVIDE("/") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForDivisionOperator(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateDivision(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForDoubleOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_POWER("^") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculatePower(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForDoubleOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_AND("&&") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForLogicOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateLogicAnd(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForLogicOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_OR("||") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForLogicOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateLogicOr(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForLogicOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_NOT("!") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForUnaryLogicOperators(operands[0]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 1) {
                throw new IllegalArgumentException(String.format("Required 1 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateLogicNot(operands[0]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForLogicOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_EQUAL("==") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForEqualityOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateEquality(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForLogicOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_NOT_EQUAL("!=") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForEqualityOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateInequality(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForLogicOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_GREATER(">") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateGreaterThan(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForLogicOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_GREATER_EQUAL(">=") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateGreaterOrEqual(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForLogicOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_LESS("<") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateLessThan(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForLogicOperator(operands[0], operands[1]);
        }
    },
    OPERATOR_LESS_EQUAL("<=") {
        @Override
        public boolean isValid(ValidityVisitor visitor, ReturnType... operands) {
            return operands.length == 2 && visitor.validateForNumberOperators(operands[0], operands[1]);
        }

        @Override
        public Object calculate(CalculationVisitor visitor, Object... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.calculateLessOrEqual(operands[0], operands[1]);
        }

        @Override
        public ReturnType returns(ReturnVisitor visitor, ReturnType... operands) {
            if (operands.length != 2) {
                throw new IllegalArgumentException(String.format("Required 2 operands, but %d were supplied", operands.length));
            }
            return visitor.returnForLogicOperator(operands[0], operands[1]);
        }
    };

    private String label;

    public String getLabel() {
        return label;
    }

    abstract public boolean isValid(ValidityVisitor visitor, ReturnType... operands);

    abstract public Object calculate(CalculationVisitor visitor, Object... operands);

    abstract public ReturnType returns(ReturnVisitor visitor, ReturnType... operands);

    OperatorType(String label) {
        this.label = label;
    }
}
