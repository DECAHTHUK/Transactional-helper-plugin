package ru.decahthuk.transactionhelperplugin.service.staticservice;

import org.jetbrains.annotations.Nullable;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class TransactionalTreeAnalyzer {

    private static final List<TransactionalPropagation> NON_CREATING_PROPAGATIONS =
            Arrays.asList(TransactionalPropagation.SUPPORTS, TransactionalPropagation.NEVER, TransactionalPropagation.NOT_SUPPORTED);

    private TransactionalTreeAnalyzer() {
    }

    /**
     * Searches if there is any parent transaction excluding the current method (which is called)
     *
     * @param called - tree of calls (called method should be passed)
     * @return - there is higher level transaction going on
     */
    @Nullable
    public static Boolean treeContainsUpperLevelTransactionalWithoutCurrent(Node<TransactionInformationPayload> called) {
        if (called.isLeaf()) {
            return null;
        }
        Set<Node<TransactionInformationPayload>> callers = called.getChildren();
        for (Node<TransactionInformationPayload> caller : callers) {
            boolean branch = treeContainsUpperLevelTransaction(caller, called);
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
     * @param previous - called method of param 'called'
     * @return - there is higher level transaction going on
     */
    private static boolean treeContainsUpperLevelTransaction(Node<TransactionInformationPayload> called,
                                                             Node<TransactionInformationPayload> previous) {
        Set<Node<TransactionInformationPayload>> callers = called.getChildren();
        TransactionInformationPayload calledData = called.getData();
        if (called.isLeaf()) {
            return treeContainsUpperLevelTransactionLeafCheck(calledData, previous.getData());
        }
        Boolean prevLambdaCheck = performTreeContainsUpperLevelTransactionLambdaCheck(previous.getData(), calledData);
        if (prevLambdaCheck != null) {
            return prevLambdaCheck;
        }
        for (Node<TransactionInformationPayload> caller : callers) {
            TransactionInformationPayload callerData = caller.getData();
            Boolean lambdaCheck = performTreeContainsUpperLevelTransactionLambdaCheck(calledData, callerData);
            if (lambdaCheck != null) {
                return lambdaCheck;
            }
            if (calledData.isTransactional() && calledData.methodIsCorrectlySelfInvokedFromMethod(callerData)) {
                TransactionalPropagation propagation = calledData.getPropagation();
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
            if (treeContainsUpperLevelTransaction(caller, called)) {
                return true;
            }
        }
        return false;
    }

    private static Boolean performTreeContainsUpperLevelTransactionLambdaCheck(
            TransactionInformationPayload calledData,
            TransactionInformationPayload callerData) {
        if (calledData == null || callerData == null) {
            return null;
        }
        String callerMethodName = callerData.getMethodIdentifier();
        if (calledData.containsLambdaReferencesFromMethod(callerMethodName)) {
            if (calledData.allCallsAreLambdaReferences(callerMethodName) &&
                    (calledData.allLambdaReferencesIsOfPropagation(callerMethodName, TransactionalPropagation.NOT_SUPPORTED)
                            || calledData.allLambdaReferencesIsOfPropagation(callerMethodName, TransactionalPropagation.NEVER))) {
                return false;
            }
            if (calledData.anyLambdaReferenceNotInListOfPropagations(callerMethodName, NON_CREATING_PROPAGATIONS)) {
                return true;
            }
        }
        return null;
    }

    /**
     * Searches if there is any branch with no ongoing transaction excluding the current method (which is called)
     *
     * @param called - tree of calls (called method should be passed)
     * @return - boolean value if the branch with no ongoing is present
     */
    @Nullable
    public static Boolean treeBranchContainsNoTransactionWithoutCurrent(Node<TransactionInformationPayload> called) {
        if (called.isLeaf()) {
            return null;
        }
        Set<Node<TransactionInformationPayload>> callers = called.getChildren();
        for (Node<TransactionInformationPayload> caller : callers) {
            boolean branch = treeBranchContainsNoTransaction(caller, called);
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
    public static boolean treeBranchContainsNoTransactionWithCurrent(Node<TransactionInformationPayload> called) {
        return treeBranchContainsNoTransaction(called, new Node<>());
    }

    /**
     * Searches if there is any branch with no ongoing transaction
     *
     * @param called - tree of calls (called method should be passed)
     * @param previous - called method of param method 'called'
     * @return - boolean value if the branch with no ongoing is present
     */
    private static boolean treeBranchContainsNoTransaction(Node<TransactionInformationPayload> called,
                                                           Node<TransactionInformationPayload> previous) {
        Set<Node<TransactionInformationPayload>> callers = called.getChildren();
        TransactionInformationPayload calledData = called.getData();
        if (called.isLeaf()) {
            return treeBranchContainsNoTransactionLeafCheck(calledData, previous.getData());
        }
        Boolean prevLambdaCheck = performTreeBranchContainsNoTransactionLambdaCheck(previous.getData(), calledData);
        if (Boolean.TRUE.equals(prevLambdaCheck)) {
            return true;
        }
        for (Node<TransactionInformationPayload> caller : callers) {
            TransactionInformationPayload callerData = caller.getData();
            Boolean lambdaCheck = performTreeBranchContainsNoTransactionLambdaCheck(calledData, callerData);
            if (Boolean.TRUE.equals(lambdaCheck)) {
                return true;
            } else if (Boolean.FALSE.equals(lambdaCheck)) {
                continue;
            }
            if (calledData.isTransactional() && calledData.methodIsCorrectlySelfInvokedFromMethod(callerData)) {
                TransactionalPropagation propagation = calledData.getPropagation();
                if (propagation == TransactionalPropagation.NOT_SUPPORTED || propagation == TransactionalPropagation.NEVER) {
                    return true;
                }
                if (!TransactionalPropagation.SUPPORTS.equals(propagation)) {
                    continue;
                }
            }
            if (treeBranchContainsNoTransaction(caller, called)) {
                return true;
            }
        }
        return false;
    }

    private static Boolean performTreeBranchContainsNoTransactionLambdaCheck(
            TransactionInformationPayload calledData,
            TransactionInformationPayload callerData) {
        if (calledData == null || callerData == null) {
            return null;
        }
        String callerMethodName = callerData.getMethodIdentifier();
        if (calledData.containsLambdaReferencesFromMethod(callerMethodName) &&
                !calledData.anyLambdaReferenceIsNotTransactional(callerMethodName)) {
            if (calledData.anyLambdaReferenceIsOfPropagation(callerMethodName, TransactionalPropagation.NOT_SUPPORTED)
                    || calledData.anyLambdaReferenceIsOfPropagation(callerMethodName, TransactionalPropagation.NEVER)) {
                return true;
            }
            if (calledData.noneLambdaReferencesInListOfPropagations(callerMethodName, NON_CREATING_PROPAGATIONS)) {
                return false;
            }
        }
        return null;
    }

    /**
     * Does calculations in case of leaf in ContainsUpperLevelTransaction scenario
     * @param callerData - leaf data
     * @param calledData - previously called method(which is called by current leaf)
     * @return - there is higher level transaction going on
     */
    private static boolean treeContainsUpperLevelTransactionLeafCheck(TransactionInformationPayload callerData,
                                                                      TransactionInformationPayload calledData) {
        Boolean lambdaCheck = performTreeContainsUpperLevelTransactionLambdaCheck(calledData, callerData);
        if (lambdaCheck != null) {
            return lambdaCheck;
        }
        if (callerData.isTransactional()) {
            TransactionalPropagation propagation = callerData.getPropagation();
            if (propagation == TransactionalPropagation.NOT_SUPPORTED || propagation == TransactionalPropagation.NEVER) {
                return false;
            }
            return !TransactionalPropagation.SUPPORTS.equals(propagation);
        }
        return false;
    }

    /**
     * Does calculations in case of leaf in ContainsNoTransaction scenario
     * @param callerData - leaf data
     * @param calledData - previously called method(which is called by current leaf)
     * @return - boolean value if the branch with no ongoing is present
     */
    private static boolean treeBranchContainsNoTransactionLeafCheck(TransactionInformationPayload callerData,
                                                                    TransactionInformationPayload calledData) {
        Boolean lambdaCheck = performTreeBranchContainsNoTransactionLambdaCheck(calledData, callerData);
        if (lambdaCheck != null) {
            return lambdaCheck;
        }
        if (callerData.isTransactional()) {
            TransactionalPropagation propagation = callerData.getPropagation();
            if (propagation == TransactionalPropagation.NOT_SUPPORTED || propagation == TransactionalPropagation.NEVER) {
                return true;
            }
            return TransactionalPropagation.SUPPORTS.equals(propagation);
        }
        return true;
    }
}
