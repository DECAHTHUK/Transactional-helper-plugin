package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import ru.decahthuk.transactionhelperplugin.model.EntityClassInformation;

import java.util.Map;

public final class EntityMethodAnalyzer {

    private EntityMethodAnalyzer() {
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
