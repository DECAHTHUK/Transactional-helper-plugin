package ru.decahthuk.transactionhelperplugin.service.utils;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.model.EntityClassInformation;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.service.TransactionalSearcherService;

import java.util.Map;
import java.util.Objects;

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

    public static boolean methodCallExpressionIsIncorrectClassLevelInvocation(@NotNull PsiElement reference) {
        PsiMethodCallExpression call = PsiTreeUtil.getParentOfType(reference, PsiMethodCallExpression.class);
        if (call != null) {
            return methodCallExpressionIsIncorrectClassLevelInvocation(call);
        }
        return false;
    }

    public static boolean methodCallExpressionIsIncorrectClassLevelInvocation(@NotNull PsiMethodCallExpression expression) {
        PsiMethod calledMethod = expression.resolveMethod();
        if (calledMethod != null) {
            PsiClass calledMethodContainingClass = calledMethod.getContainingClass();
            PsiClass callerMethodContainingClass = PsiTreeUtil.getParentOfType(expression, PsiClass.class);
            if (calledMethodContainingClass != null && Objects.equals(calledMethodContainingClass, callerMethodContainingClass)) {
                return methodCallIsIncorrectlySelfInvoked(expression, callerMethodContainingClass);
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

    @Deprecated
    private static boolean lambdaReferencePresent(@NotNull PsiClass callerMethodContainingClass,
                                                  @NotNull PsiClass calledMethodContainingClass,
                                                  @NotNull PsiMethodCallExpression expression) {
        if (callerMethodContainingClass.equals(calledMethodContainingClass)) {
            PsiLambdaExpression lambdaExpression = PsiTreeUtil.getParentOfType(expression, PsiLambdaExpression.class);
            if (lambdaExpression != null) {
                PsiMethodCallExpression lambdaMethodCallExpression = PsiTreeUtil.getParentOfType(lambdaExpression, PsiMethodCallExpression.class);
                return lambdaMethodCallExpression != null;
            }
            return false;
        }
        return false;
    }

    private static boolean methodCallIsIncorrectlySelfInvoked(@NotNull PsiMethodCallExpression call, @NotNull PsiClass containingClass) {
        PsiExpression expression = call.getMethodExpression().getQualifierExpression();
        if (expression != null) {
            PsiType psiType = expression.getType();
            if (psiType instanceof PsiClassReferenceType) {
                PsiClassReferenceType psiClassReferenceType = (PsiClassReferenceType) psiType;
                PsiClass methodContainer = psiClassReferenceType.resolve();
                if (methodContainer != null && methodContainer.getQualifiedName() != null) {
                    return !Objects.equals(methodContainer.getQualifiedName(),
                            containingClass.getQualifiedName());
                }
            }
        }
        return true;
    }
}
