package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.model.EntityClassInformation;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;

import java.util.Map;

public final class TransactionalMethodAnalyzer {

    private TransactionalMethodAnalyzer() {
    }

    public static PsiMethod getEnclosingMethod(PsiElement element) {
        while (element != null) {
            if (element instanceof PsiMethod) {
                return (PsiMethod) element;
            }
            element = element.getParent();
        }
        return null;
    }

    public static boolean methodCallExpressionIsClassLevelInvocation(@NotNull PsiMethodCallExpression expression) {
        PsiMethod calledMethod = expression.resolveMethod();
        if (calledMethod != null) {
            PsiClass calledMethodContainingClass = calledMethod.getContainingClass();
            PsiClass callerMethodContainingClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
            if (calledMethodContainingClass != null && callerMethodContainingClass != null) {
                if (callerMethodContainingClass.equals(calledMethodContainingClass)) {
                    PsiLambdaExpression lambdaExpression = PsiTreeUtil.getParentOfType(expression, PsiLambdaExpression.class);
                    if (lambdaExpression != null) {
                        PsiMethodCallExpression lambdaMethodCallExpression = PsiTreeUtil.getParentOfType(lambdaExpression, PsiMethodCallExpression.class);
                        return lambdaMethodCallExpression == null;
                    }
                    return true;
                }

            }
        }
        return false;
    }

    public static boolean methodsAreTransactionalSelfInvoked(PsiMethod calledMethod, PsiMethod methodThatCalls) {
        TransactionInformationPayload calledMethodPayload = TransactionalSearcherService
                .buildTransactionInformationPayload(calledMethod);
        if (calledMethodPayload.isTransactional()) {
            TransactionInformationPayload methodThatCallsPayload = TransactionalSearcherService
                    .buildTransactionInformationPayload(methodThatCalls);
            return !methodThatCallsPayload.isTransactional();
        }
        return false;
    }

    public static boolean isCallALazyInitializedEntity(Map<String, EntityClassInformation> entityClassInformationMap, PsiMethodCallExpression call) {
        PsiMethod resolvedMethod = call.resolveMethod();
        if (resolvedMethod != null) {
            PsiClass containingClass = resolvedMethod.getContainingClass();
            if (containingClass != null) {
                String className = containingClass.getQualifiedName();
                if (entityClassInformationMap.containsKey(className)) {
                    EntityClassInformation entityClassInformation = entityClassInformationMap.get(className);
                    String methodName = call.getMethodExpression().getReferenceName();
                    return entityClassInformation.getLazyFieldGetters().contains(methodName);
                }
            }
        }
        return false;
    }
}
