package ru.decahthuk.transactionhelperplugin.service;

import org.jetbrains.annotations.Nullable;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;
import ru.decahthuk.transactionhelperplugin.utils.PsiAnnotationUtils;

import java.util.Set;

public final class TransactionalTreeAnalyzer {

    private TransactionalTreeAnalyzer() {
    }

    @Nullable
    public static Boolean treeContainsUpperLevelTransactionalWithoutCurrent(Node<TransactionInformationPayload> called) {
        if (called.isLeaf()) {
            return null;
        }
        Set<Node<TransactionInformationPayload>> callers = called.getChildren();
        for (Node<TransactionInformationPayload> caller : callers) {
            boolean branch = treeContainsUpperLevelTransactional(caller);
            if (branch) {
                return true;
            }
        }
        return false;
    }

    /**
     * Searches if there is any parent transaction
     *
     * @param called - tree of calls (called method should be passed)
     * @return - there is higher level transaction going on
     */
    private static boolean treeContainsUpperLevelTransactional(Node<TransactionInformationPayload> called) {
        Set<Node<TransactionInformationPayload>> callers = called.getChildren();
        TransactionInformationPayload calledData = called.getData();
        if (called.isLeaf()) {
            TransactionalPropagation propagation = PsiAnnotationUtils.getPropagationArg(calledData.getArgs());
            if (propagation == TransactionalPropagation.NOT_SUPPORTED) {
                return false;
            }
            if (propagation == TransactionalPropagation.NEVER) {
                return false;
            }
            return calledData.isTransactional() && !TransactionalPropagation.SUPPORTS.equals(propagation);
        }
        for (Node<TransactionInformationPayload> caller : callers) {
            TransactionInformationPayload callerData = caller.getData();
            if (calledData.isTransactional() && !calledData.isMethodIsIncorrectlySelfInvokedFromMethod(callerData)) {
                TransactionalPropagation propagation = PsiAnnotationUtils.getPropagationArg(calledData.getArgs());
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
            if (treeContainsUpperLevelTransactional(caller)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static Boolean treeBranchContainsNoTransactionWithoutCurrent(Node<TransactionInformationPayload> called) {
        if (called.isLeaf()) {
            return null;
        }
        Set<Node<TransactionInformationPayload>> callers = called.getChildren();
        for (Node<TransactionInformationPayload> caller : callers) {
            boolean branch = treeBranchContainsNoTransaction(caller);
            if (branch) {
                return true;
            }
        }
        return false;
    }

    /**
     * Searches if there is any branch with no ongoing transaction
     *
     * @param called - tree of calls (called method should be passed)
     * @return - boolean value if the branch with no ongoing is present
     */
    private static boolean treeBranchContainsNoTransaction(Node<TransactionInformationPayload> called) {
        Set<Node<TransactionInformationPayload>> callers = called.getChildren();
        TransactionInformationPayload calledData = called.getData();
        if (called.isLeaf()) {
            TransactionalPropagation propagation = PsiAnnotationUtils.getPropagationArg(calledData.getArgs());
            if (propagation == TransactionalPropagation.NOT_SUPPORTED) {
                return true;
            }
            if (propagation == TransactionalPropagation.NEVER) {
                return true;
            }
            return !calledData.isTransactional() || TransactionalPropagation.SUPPORTS.equals(propagation);
        }
        for (Node<TransactionInformationPayload> caller : callers) {
            TransactionInformationPayload callerData = caller.getData();
            if (calledData.isTransactional() && !calledData.isMethodIsIncorrectlySelfInvokedFromMethod(callerData)) {
                TransactionalPropagation propagation = PsiAnnotationUtils.getPropagationArg(calledData.getArgs());
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
            if (treeBranchContainsNoTransaction(caller)) {
                return true;
            }
        }
        return false;
    }
}
