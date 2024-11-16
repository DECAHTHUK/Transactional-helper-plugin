package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.psi.PsiClass;
import org.apache.commons.collections.CollectionUtils;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;
import ru.decahthuk.transactionhelperplugin.utils.AnnotationUtils;

import java.util.List;
import java.util.Optional;

public final class TransactionalTreeAnalyzer {

    private TransactionalTreeAnalyzer() {
    }

    /**
     * Searches if there is any parent transaction
     *
     * @param tree - tree of calls
     * @param classLevelInvocation - indicator that method caller is in the same class (proxy won't work). Default true
     * @return - there is higher level transaction going on
     */
    public static boolean treeContainsUpperLevelTransactional(Node<TransactionInformationPayload> tree,
                                                              boolean classLevelInvocation) {
        List<Node<TransactionInformationPayload>> children = tree.getChildren();
        if (CollectionUtils.isEmpty(tree.getChildren())) {
            return false;
        }
        TransactionInformationPayload currentData = tree.getData();
        for (Node<TransactionInformationPayload> child : children) {
            TransactionInformationPayload childData = child.getData();
            if (classLevelInvocation) {
                String currentClassName = Optional.ofNullable(currentData.getPsiMethod().getContainingClass())
                        .map(PsiClass::getQualifiedName).orElse("null");
                String childClassName = Optional.ofNullable(childData.getPsiMethod().getContainingClass())
                        .map(PsiClass::getQualifiedName).orElse("null");
                if (!currentClassName.equals(childClassName)) {
                    classLevelInvocation = false;
                }
            }
            if (!classLevelInvocation && childData.isTransactional()) {
                TransactionalPropagation propagation = AnnotationUtils.getPropagationArg(childData.getArgs());
                if (propagation == TransactionalPropagation.NOT_SUPPORTED) {
                    continue;
                }
                if (!TransactionalPropagation.SUPPORTS.equals(propagation)) {
                    return true;
                }
            }
            return treeContainsUpperLevelTransactional(child, classLevelInvocation);
        }
        return false;
    }
}
