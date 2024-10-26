package ru.decahthuk.transactionhelperplugin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Узел древовидной структуры
 *
 * @param <T>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Node<T> {
    private T data;
    private Node<T> parent;
    private List<Node<T>> children;

    public Node(T data, Node<T> parent) {
        this.data = data;
        this.parent = parent;
    }

    public void addChild(Node<T> child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public boolean isLeaf() {
        return CollectionUtils.isEmpty(children);
    }

    public boolean isRoot() {
        return parent == null;
    }
}