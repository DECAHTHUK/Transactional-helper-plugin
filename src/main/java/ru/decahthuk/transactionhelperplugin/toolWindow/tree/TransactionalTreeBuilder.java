package ru.decahthuk.transactionhelperplugin.toolWindow.tree;

import lombok.experimental.UtilityClass;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;

import javax.swing.tree.DefaultTreeModel;

@UtilityClass
public class TransactionalTreeBuilder {

    public DefaultTreeModel buildTree(Node<TransactionInformationPayload> transactionInfo) {
        UINavigatableTreeNode root = fillData(transactionInfo);
        if (!transactionInfo.isLeaf()) {
            for (Node<TransactionInformationPayload> child : transactionInfo.getChildren()) {
                buildTreeInner(child, root);
            }
        }
        return new DefaultTreeModel(root);
    }

    private void buildTreeInner(Node<TransactionInformationPayload> transactionInfo, UINavigatableTreeNode parent) {
        UINavigatableTreeNode newNode = fillData(transactionInfo);
        parent.add(newNode);
        if (!transactionInfo.isLeaf()) {
            for (Node<TransactionInformationPayload> child : transactionInfo.getChildren()) {
                buildTreeInner(child, newNode);
            }
        }
    }

    private UINavigatableTreeNode fillData(Node<TransactionInformationPayload> transactionInfo) {
        TransactionInformationPayload payload = transactionInfo.getData();
        return new UINavigatableTreeNode(payload.getMethodIdentifier(), payload.getNavigatable(), payload);
    }
}