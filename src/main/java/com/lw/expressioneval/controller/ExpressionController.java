package com.lw.expressioneval.controller;

import com.lw.expressioneval.entities.dtos.ExpressionCreateDto;
import com.lw.expressioneval.service.ExpressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ExpressionController {
    private ExpressionService expressionService;

    @Autowired
    public ExpressionController(ExpressionService expressionService) {
        this.expressionService = expressionService;
    }

    @PostMapping("/expression")
    public ResponseEntity<Object> addExpression(@RequestBody ExpressionCreateDto expression) {
        Long id = expressionService.save(expression);
        return new ResponseEntity<>(Map.of("expressionId", id), HttpStatus.OK);
    }

    @PostMapping("/evaluate/{expressionId}")
    public ResponseEntity<Object> evaluate(@PathVariable Long expressionId, @RequestBody Map<String, Object> requestMap) {
        boolean result = expressionService.evaluate(expressionId, requestMap);
        return new ResponseEntity<>(Map.of("expressionResult", result), HttpStatus.OK);
    }
}
