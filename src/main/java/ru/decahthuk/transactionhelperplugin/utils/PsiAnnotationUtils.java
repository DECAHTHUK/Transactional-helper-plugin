package ru.decahthuk.transactionhelperplugin.utils;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.decahthuk.transactionhelperplugin.utils.Constants.TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME;
import static ru.decahthuk.transactionhelperplugin.utils.Constants.TRANSACTIONAL_PROPAGATION_ARG_NAME;

public final class PsiAnnotationUtils {

    private PsiAnnotationUtils() {
    }

    public static List<PsiAnnotation> getMethodLevelAndClassLevelAnnotations(PsiMethod method) {
        List<PsiAnnotation> annotations = Arrays.stream(method.getAnnotations()).collect(Collectors.toList());
        PsiClass containingClass = method.getContainingClass();
        if (containingClass != null) {
            annotations.addAll(List.of(containingClass.getAnnotations()));
        }
        return annotations;
    }

    public static boolean annotationIsTransactionalWithPropagation(PsiAnnotation annotation, TransactionalPropagation propagation) {
        String annotationName = annotation.getQualifiedName();
        if (Objects.equals(annotationName, TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME)) {
            TransactionalPropagation actualPropagation = getPropagationArg(annotation);
            return propagation.equals(actualPropagation);
        }
        return false;
    }

    public static TransactionalPropagation getPropagationArg(PsiAnnotation annotation) {
        Map<String, String> args = parseAnnotationArgs(annotation);
        return getPropagationArg(args);
    }

    public static TransactionalPropagation getPropagationArg(Map<String, String> args) {
        if (args != null && args.containsKey(TRANSACTIONAL_PROPAGATION_ARG_NAME)) {
            return TransactionalPropagation.fromString(args.get(TRANSACTIONAL_PROPAGATION_ARG_NAME));
        }
        return null;
    }

    public static Map<String, String> parseAnnotationArgs(PsiAnnotation annotation) {
        if (annotation == null) {
            return null;
        }
        PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
        return Arrays.stream(attributes).collect(Collectors.toMap(PsiNameValuePair::getAttributeName,
                PsiAnnotationUtils::getAttributeValue, (t1, t2) -> t2));
    }

    private static String getAttributeValue(PsiNameValuePair psiNameValuePair) {
        return Optional.ofNullable(psiNameValuePair)
                .map(PsiNameValuePair::getValue)
                .map(PsiAnnotationMemberValue::getText)
                .orElse("");
    }
}
