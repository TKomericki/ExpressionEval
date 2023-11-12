package com.lw.expressioneval.entities;

import com.lw.expressioneval.evaluator.enums.OperatorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "syntax_tree_node")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpressionNode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type")
    private NodeType nodeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tree_id", nullable = false)
    private ExpressionTree tree;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "left_operand", referencedColumnName = "id")
    private ExpressionNode leftOperand;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "right_operand", referencedColumnName = "id")
    private ExpressionNode rightOperand;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator_type")
    private OperatorType operator;

    @Column(name = "node_value")
    private String nodeValue;

    public ExpressionNode(NodeType nodeType, ExpressionTree tree, ExpressionNode leftOperand, ExpressionNode rightOperand, OperatorType operator, String nodeValue) {
        this.nodeType = nodeType;
        this.tree = tree;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
        this.nodeValue = nodeValue;
    }

    @Override
    public String toString() {
        return "ExpressionNode{" +
                "id=" + id +
                ", nodeType='" + nodeType + '\'' +
                ", tree=" + tree +
                ", leftOperand=" + leftOperand +
                ", rightOperand=" + rightOperand +
                ", type=" + operator +
                ", nodeValue='" + nodeValue + '\'' +
                '}';
    }
}
