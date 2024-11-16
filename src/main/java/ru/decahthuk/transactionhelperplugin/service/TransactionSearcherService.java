package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import lombok.extern.slf4j.Slf4j;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.decahthuk.transactionhelperplugin.utils.AnnotationUtils.parseAnnotationArgs;
import static ru.decahthuk.transactionhelperplugin.utils.Constants.TEST_CLASS_POSTFIX;
import static ru.decahthuk.transactionhelperplugin.utils.Constants.TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME;

@Slf4j
@Service(Service.Level.PROJECT)
public final class TransactionSearcherService implements Disposable {

    private final Project project;

    //TODO: Использовать кэш как фичу. Типо встретил проблемы с производительностью и начал кэшировать. Позже. + Some sort of expiry
    private final Map<String, Node<PsiMethod>> cache = new HashMap<>();

    public TransactionSearcherService(Project project) {
        this.project = project;
    }

    public Node<TransactionInformationPayload> buildUsageTree(PsiMethod method) {
        return buildUsageTreeInner(method, null, null);
    }

    public Node<TransactionInformationPayload> buildUsageTreeWithBenchmarking(PsiMethod method) {
        LocalDateTime before = LocalDateTime.now();
        AtomicInteger methodCounter = new AtomicInteger(0);
        Node<TransactionInformationPayload> out = buildUsageTreeInner(method, methodCounter, null);
        LocalDateTime after = LocalDateTime.now();
        System.out.println("Millisecs to run recursion = " + ChronoUnit.MILLIS.between(before, after));
        System.out.println("Methods counter = " + methodCounter.get());
        return out;
    }

    private Node<TransactionInformationPayload> buildUsageTreeInner(
            PsiMethod method, AtomicInteger methodCounter, Node<TransactionInformationPayload> rootNode) {
        Node<TransactionInformationPayload> newNode = new Node<>(buildTransactionInformationPayload(method), rootNode);
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
                PsiClass containingClass = containingMethod.getContainingClass();
                // Excluding test classes. May make mechanism better
                if (containingClass == null || !String.valueOf(containingClass.getQualifiedName()).endsWith(TEST_CLASS_POSTFIX)) {
                    visitedMethods.add(containingMethod);
                    buildUsageTreeInner(containingMethod, methodCounter, newNode);
                }
            }
        });
        return newNode;
    }

    private TransactionInformationPayload buildTransactionInformationPayload(PsiMethod method) {
        TransactionInformationPayload payload = new TransactionInformationPayload();
        Map<String, String> transactionalArgs = searchTransactionalData(method);
        payload.setPsiMethod(method);
        payload.setTransactional(transactionalArgs != null);
        payload.setArgs(transactionalArgs);
        return payload;
    }

    private Map<String, String> searchTransactionalData(PsiMethod method) {
        PsiAnnotation annotation = method.getModifierList().findAnnotation(TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME);
        if (annotation == null) { // Searching less prior Class-level annotations
            PsiClass containingClass = method.getContainingClass();
            if (containingClass != null) {
                annotation = Optional.ofNullable(containingClass.getModifierList())
                        .map(t -> t.findAnnotation(TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME)).orElse(null);
            }
        }
        return parseAnnotationArgs(annotation);
    }

    private void cacheEvict() {
        cache.clear();
    }

    @Override
    public void dispose() {
        cacheEvict();
    }
}
