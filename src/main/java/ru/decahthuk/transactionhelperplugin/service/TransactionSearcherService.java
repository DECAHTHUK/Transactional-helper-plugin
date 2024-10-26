package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.openapi.components.Service;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import ru.decahthuk.transactionhelperplugin.model.Node;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public final class TransactionSearcherService {

    //TODO: Использовать кэш как фичу. Типо встретил проблемы с производительностью и начал кэшировать. Позже. + Some sort of expiry
    private final Map<String, Node<PsiMethod>> cache = new HashMap<>();

    public Node<PsiMethod> buildUsageTree(PsiMethod method) {
        return buildUsageTreeInner(method, null, null);
    }

    public Node<PsiMethod> buildUsageTreeWithBenchmarking(PsiMethod method) {
        LocalDateTime before = LocalDateTime.now();
        AtomicInteger methodCounter = new AtomicInteger(0);
        Node<PsiMethod> out = buildUsageTreeInner(method, methodCounter, null);
        LocalDateTime after = LocalDateTime.now();
        System.out.println("Millisecs to run recursion = " + ChronoUnit.MILLIS.between(before, after));
        System.out.println("Methods counter = " + methodCounter.get());
        return out;
    }

    private Node<PsiMethod> buildUsageTreeInner(PsiMethod method, AtomicInteger methodCounter, Node<PsiMethod> rootNode) {
        Node<PsiMethod> newNode = new Node<>(method, rootNode);
        Optional.ofNullable(rootNode).ifPresent(t -> t.addChild(newNode));

        if (methodCounter != null) {
            methodCounter.incrementAndGet();
        }

        Query<PsiReference> query = ReferencesSearch.search(method);
        Set<PsiMethod> visitedMethods = new HashSet<>();

        query.forEach(reference -> {
            PsiElement element = reference.getElement();
            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            if (containingMethod != null && !visitedMethods.contains(containingMethod)) {
                visitedMethods.add(containingMethod);
                buildUsageTreeInner(containingMethod, methodCounter, newNode);
            }
        });
        return newNode;
    }
}
