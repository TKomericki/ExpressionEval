package com.lw.expressioneval.repository;

import com.lw.expressioneval.entities.ExpressionNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpressionNodeRepository extends JpaRepository<ExpressionNode, Long> {
}
