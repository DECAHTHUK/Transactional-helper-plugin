package ru.decahthuk.transactionhelperplugin;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageViewManager;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionAnnotationHereInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                super.visitMethod(method);
                PsiAnnotation[] annotations = method.getAnnotations();
                for (PsiAnnotation annotation : annotations) {
                    String annotationName = annotation.getQualifiedName();
                    if (annotationName.equals("org.springframework.transaction.annotation.Transactional")) {
                        holder.registerProblem(annotation,
                                InspectionBundle.message("inspection.transaction.annotation.here.descriptor"));

                        List<PsiElement> psiElements = new ArrayList<>();

                        ReferencesSearch.search(method).forEach(reference -> {
                            PsiElement psiElement = reference.getElement();
                            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
                            PsiElement containingClass = PsiUtil.getTopLevelClass(containingMethod);
                            psiElements.add(psiElement);
                        });
                        holder.registerProblem(method, psiElements.toString());

                        buildUsageTree(method, 0);
                    }
                }
            }
        };
    }

    public static void buildUsageTree(PsiMethod method, int level) {
        String indent = "  ".repeat(level);
        System.out.println(indent + "Method: " + method.getName() + " in Class: " + method.getContainingClass().getQualifiedName());

        Query<PsiReference> query = ReferencesSearch.search(method);
        Set<PsiMethod> visitedMethods = new HashSet<>();

        query.forEach(reference -> {
            PsiElement element = reference.getElement();
            if (element != null) {
                PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
                if (containingMethod != null && !visitedMethods.contains(containingMethod)) {
                    visitedMethods.add(containingMethod);
                    buildUsageTree(containingMethod, level + 1);
                }
            }
        });
    }
}
