package com.lw.expressioneval.service;

import com.lw.expressioneval.entities.dtos.ExpressionCreateDto;

import java.util.Map;

public interface ExpressionService {
    Long save(ExpressionCreateDto expression);

    Boolean evaluate(Long id, Map<String, Object> map);
}
