package ru.decahthuk.transactionhelperplugin.utils;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiNameValuePair;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class AnnotationUtils {

    private AnnotationUtils() {
    }

    public static Map<String, String> parseAnnotationArgs(PsiAnnotation annotation) {
        if (annotation == null) {
            return null;
        }
        PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
        return Arrays.stream(attributes).collect(Collectors.toMap(PsiNameValuePair::getAttributeName,
                AnnotationUtils::getAttributeValue, (t1, t2) -> t2));
    }

    private static String getAttributeValue(PsiNameValuePair psiNameValuePair) {
        return Optional.ofNullable(psiNameValuePair)
                .map(PsiNameValuePair::getValue)
                .map(PsiAnnotationMemberValue::getText)
                .orElse(null);
    }
}
