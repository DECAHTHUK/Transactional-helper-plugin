package ru.decahthuk.transactionhelperplugin.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;

import java.util.Optional;

public final class PsiMethodUtils {

    private PsiMethodUtils() {
    }

    public static String getClassLevelUniqueMethodName(PsiMethod method) {
        String methodName = method.getName();

        PsiParameter[] parameters = method.getParameterList().getParameters();
        StringBuilder parameterTypes = new StringBuilder();
        for (PsiParameter parameter : parameters) {
            PsiType type = parameter.getType();
            parameterTypes.append(type.getCanonicalText()).append(",");
        }
        if (!parameterTypes.isEmpty()) {
            parameterTypes.setLength(parameterTypes.length() - 1); // Remove the trailing comma
        }

        return methodName + "(" + parameterTypes + ")";
    }

    public static String getClassName(PsiMethod method) {
        return Optional.ofNullable(method.getContainingClass())
                .map(PsiClass::getQualifiedName).orElse("null");
    }

    public static String getUniqueClassMethodName(PsiMethod method) {
        return getClassName(method) + "." + getClassLevelUniqueMethodName(method);
    }
}
