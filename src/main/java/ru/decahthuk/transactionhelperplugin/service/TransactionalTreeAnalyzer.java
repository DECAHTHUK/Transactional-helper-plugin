package ru.decahthuk.transactionhelperplugin.service;

import org.apache.commons.collections.CollectionUtils;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;
import ru.decahthuk.transactionhelperplugin.utils.PsiAnnotationUtils;

import java.util.Set;

public final class TransactionalTreeAnalyzer {

    private TransactionalTreeAnalyzer() {
    }

    public static boolean treeBranchContainsNoTransaction(Node<TransactionInformationPayload> tree) {
        return treeBranchContainsNoTransaction(tree, true);
    }

    /**
     * Searches if there is any parent transaction
     *
     * @param tree - tree of calls
     * @return - there is higher level transaction going on
     */
    public static boolean treeContainsUpperLevelTransactional(Node<TransactionInformationPayload> tree) {
        return treeContainsUpperLevelTransactional(tree, true);
    }

    /**
     * Searches if there is any parent transaction
     *
     * @param tree - tree of calls
     * @param classLevelInvocation - indicator that method caller is in the same class (proxy won't work). Default true
     * @return - there is higher level transaction going on
     */
    private static boolean treeContainsUpperLevelTransactional(Node<TransactionInformationPayload> tree,
                                                              boolean classLevelInvocation) {
        Set<Node<TransactionInformationPayload>> children = tree.getChildren();
        if (CollectionUtils.isEmpty(tree.getChildren())) {
            return false;
        }
        TransactionInformationPayload currentData = tree.getData();
        for (Node<TransactionInformationPayload> child : children) {
            TransactionInformationPayload childData = child.getData();
            boolean classLevelInvocationConcrete = calculateClassLevelInvocation(currentData, childData, classLevelInvocation);
            if (!classLevelInvocationConcrete && childData.isTransactional()) {
                TransactionalPropagation propagation = PsiAnnotationUtils.getPropagationArg(childData.getArgs());
                if (propagation == TransactionalPropagation.NOT_SUPPORTED) {
                    continue;
                }
                if (propagation == TransactionalPropagation.NEVER) {
                    return false;
                }
                if (!TransactionalPropagation.SUPPORTS.equals(propagation)) {
                    return true;
                }
            }
            boolean mid = treeContainsUpperLevelTransactional(child, classLevelInvocationConcrete);
            if (mid) {
                return true;
            }
        }
        return false;
    }

    private static boolean treeBranchContainsNoTransaction(Node<TransactionInformationPayload> tree,
                                                               boolean classLevelInvocation) {
        Set<Node<TransactionInformationPayload>> children = tree.getChildren();
        if (CollectionUtils.isEmpty(tree.getChildren())) {
            return true;
        }
        TransactionInformationPayload currentData = tree.getData();
        for (Node<TransactionInformationPayload> child : children) {
            TransactionInformationPayload childData = child.getData();
            boolean classLevelInvocationConcrete = calculateClassLevelInvocation(currentData, childData, classLevelInvocation);
            if (!classLevelInvocationConcrete && childData.isTransactional()) {
                TransactionalPropagation propagation = PsiAnnotationUtils.getPropagationArg(childData.getArgs());
                if (propagation == TransactionalPropagation.NOT_SUPPORTED) {
                    return true;
                }
                if (propagation == TransactionalPropagation.NEVER) {
                    return true;
                }
                if (!TransactionalPropagation.SUPPORTS.equals(propagation)) {
                    continue;
                }
            }
            return treeBranchContainsNoTransaction(child, classLevelInvocationConcrete);
        }
        return false;
    }

    private static boolean calculateClassLevelInvocation(TransactionInformationPayload currentData, TransactionInformationPayload childData,
                                                         boolean classLevelInvocation) {
        if (classLevelInvocation) {
            String currentClassName = currentData.getClassName();
            String childClassName = childData.getClassName();
            if (!currentClassName.equals(childClassName)) {
                return false;
            }
        }
        return classLevelInvocation;
    }
}
