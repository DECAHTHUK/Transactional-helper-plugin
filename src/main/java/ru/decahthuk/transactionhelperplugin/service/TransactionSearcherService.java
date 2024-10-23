package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.openapi.components.Service;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public final class TransactionSearcherService {

    public void buildUsageTree(PsiMethod method, int level, AtomicInteger methodCounter) {
        String indent = "  ".repeat(level);
        methodCounter.incrementAndGet();
        System.out.println(indent + "Method: " + method.getName() + " in Class: " + method.getContainingClass().getQualifiedName());

        Query<PsiReference> query = ReferencesSearch.search(method);
        Set<PsiMethod> visitedMethods = new HashSet<>();

        query.forEach(reference -> {
            PsiElement element = reference.getElement();
            if (element != null) {
                PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
                if (containingMethod != null && !visitedMethods.contains(containingMethod)) {
                    visitedMethods.add(containingMethod);
                    buildUsageTree(containingMethod, level + 1, methodCounter);
                }
            }
        });
    }
}
