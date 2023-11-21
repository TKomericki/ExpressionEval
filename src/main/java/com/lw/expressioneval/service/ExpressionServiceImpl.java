package com.lw.expressioneval.service;

import com.lw.expressioneval.entities.ExpressionNode;
import com.lw.expressioneval.entities.ExpressionTree;
import com.lw.expressioneval.entities.dtos.ExpressionCreateDto;
import com.lw.expressioneval.error.BadRequestException;
import com.lw.expressioneval.error.EntityNotFoundException;
import com.lw.expressioneval.evaluator.ExpressionParser;
import com.lw.expressioneval.evaluator.expressions.Exp;
import com.lw.expressioneval.mappers.ExpressionMapper;
import com.lw.expressioneval.repository.ExpressionTreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExpressionServiceImpl implements ExpressionService {
    private ExpressionTreeRepository expressionTreeRepository;

    @Autowired
    public ExpressionServiceImpl(ExpressionTreeRepository expressionTreeRepository) {
        this.expressionTreeRepository = expressionTreeRepository;
    }

    @Override
    public Long save(ExpressionCreateDto expression) {
        if (expression.name() == null || expression.value() == null) {
            throw new BadRequestException("Wrong input, please provide expression name and value.");
        }
        if (expression.value().isEmpty()) {
            throw new BadRequestException("Provided empty logical expression");
        }
        try {
            Exp ex = ExpressionParser.parseExpression(expression.value());
            List<ExpressionNode> nodes = ExpressionMapper.expressionToNodeList(ex);
            ExpressionTree tree = new ExpressionTree(expression.name(), nodes, nodes.get(nodes.size() - 1));
            return expressionTreeRepository.save(tree).getId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new BadRequestException("'" + expression.value() + "' is not a valid expression.\n" + e.getMessage());
        }
    }

    @Override
    public Boolean evaluate(Long id, Map<String, Object> map) {
        Optional<ExpressionTree> result = expressionTreeRepository.findById(id);
        if (result.isPresent()) {
            try {
                Object evalResult = ExpressionMapper.treeToExpression(result.get()).calculate(map);
                if (!(evalResult instanceof Boolean)) {
                    throw new IllegalArgumentException("Result of the logical expression must be a boolean, got: " + result);
                }
                return (Boolean) evalResult;
            } catch (IllegalArgumentException | IllegalStateException e) {
                throw new BadRequestException(e.getMessage());
            }
        } else {
            throw new EntityNotFoundException(ExpressionTree.class, id);
        }
    }
}
