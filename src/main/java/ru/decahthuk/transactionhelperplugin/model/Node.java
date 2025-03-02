package ru.decahthuk.transactionhelperplugin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Node of a tree-like structure
 *
 * @param <T>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Node<T> {
    private T data;
    private Set<Node<T>> children;
    private int depth = 0;

    public Node(T data, Node<T> parent) {
        this.data = data;
        if (parent != null) {
            this.depth = parent.getDepth() + 1;
            parent.addChild(this);
        }
    }

    public void addChild(Node<T> child) {
        if (children == null) {
            children = new HashSet<>();
        }
        children.add(child);
    }

    public boolean isLeaf() {
        return CollectionUtils.isEmpty(children);
    }

}