package com.lw.expressioneval.repository;

import com.lw.expressioneval.entities.ExpressionTree;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpressionTreeRepository extends JpaRepository<ExpressionTree, Long> {
}
