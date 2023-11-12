package com.lw.expressioneval.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "syntax_tree")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExpressionTree {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "tree", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Column(name = "nodes", nullable = false)
    private List<ExpressionNode> expressionNodes;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "root_node_id", referencedColumnName = "id")
    private ExpressionNode rootNode;

    public ExpressionTree(String name, List<ExpressionNode> expressionNodes, ExpressionNode rootNode) {
        this.name = name;
        this.expressionNodes = new ArrayList<>(expressionNodes);
        this.rootNode = rootNode;

        for (ExpressionNode node : this.expressionNodes) {
            node.setTree(this);
        }
    }

    @Override
    public String toString() {
        return "ExpressionTree{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rootNode=" + rootNode +
                '}';
    }
}
