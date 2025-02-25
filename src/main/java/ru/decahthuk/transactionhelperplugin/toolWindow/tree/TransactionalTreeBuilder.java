package ru.decahthuk.transactionhelperplugin.toolWindow.tree;

import lombok.experimental.UtilityClass;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;

import javax.swing.tree.DefaultTreeModel;
import java.util.Optional;

@UtilityClass
public class TransactionalTreeBuilder {

    public DefaultTreeModel buildTree(Node<TransactionInformationPayload> transactionInfo) {
        UINavigatableTreeNode root = fillData(transactionInfo, null);
        if (!transactionInfo.isLeaf()) {
            for (Node<TransactionInformationPayload> child : transactionInfo.getChildren()) {
                buildTreeInner(child, root);
            }
        }
        return new DefaultTreeModel(root);
    }

    private void buildTreeInner(Node<TransactionInformationPayload> transactionInfo, UINavigatableTreeNode parent) {
        UINavigatableTreeNode newNode = fillData(transactionInfo, parent);
        parent.add(newNode);
        if (!transactionInfo.isLeaf()) {
            for (Node<TransactionInformationPayload> child : transactionInfo.getChildren()) {
                buildTreeInner(child, newNode);
            }
        }
    }

    private UINavigatableTreeNode fillData(Node<TransactionInformationPayload> transactionInfo, UINavigatableTreeNode parent) {
        TransactionInformationPayload payload = transactionInfo.getData();
        UINavigatableTreeNode treeNode = new UINavigatableTreeNode(String.format("%s(%s.class)", payload.getMethodName(), payload.getClassName()),
                payload.getNavigatable(), payload);

        TransactionInformationPayload parentPayload = Optional.ofNullable(parent).map(UINavigatableTreeNode::getPayload).orElse(null);

        // Checking for anomalies
        if (parentPayload != null && parentPayload.isTransactional() &&
                !parentPayload.methodIsCorrectlySelfInvokedFromMethod(payload.getMethodIdentifier())) {
            treeNode.setHasSelfInitIssues(true);
        } else if (parentPayload != null && parentPayload.anyLambdaReferenceIsTransactional(payload.getMethodIdentifier())) {
            treeNode.setHasTransactionalLambdaRef(true);
        }
        return treeNode;
    }
}