package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.PluginDisposable;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.utils.PsiMethodUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ru.decahthuk.transactionhelperplugin.utils.PsiAnnotationUtils.parseAnnotationArgs;
import static ru.decahthuk.transactionhelperplugin.utils.Constants.TEST_CLASS_POSTFIX;
import static ru.decahthuk.transactionhelperplugin.utils.Constants.TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME;

@Slf4j
@Service(Service.Level.PROJECT)
public final class TransactionSearcherService implements Disposable {

    private static final Logger LOG = Logger.getInstance(TransactionSearcherService.class);

    private final Project project;

    private final Lock lock = new ReentrantLock();
    private final Map<String, Node<TransactionInformationPayload>> cache = new HashMap<>();

    public TransactionSearcherService(Project project) {
        this.project = project;
        PsiManager manager = PsiManager.getInstance(project);
        manager.addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            @Override
            public void childAdded(@NotNull PsiTreeChangeEvent event) {
                handlePsiChange(event);
            }

            @Override
            public void childRemoved(@NotNull PsiTreeChangeEvent event) {
                handlePsiChange(event);
            }

            @Override
            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                handlePsiChange(event);
            }

            @Override
            public void childMoved(@NotNull PsiTreeChangeEvent event) {
                handlePsiChange(event);
            }

            @Override
            public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
                handlePsiChange(event);
            }

            private void handlePsiChange(@NotNull PsiTreeChangeEvent event) {
                if (cache.isEmpty()) {
                    return;
                }
                cacheEvict();
            }
        }, PluginDisposable.getInstance(project));
    }

    public Node<TransactionInformationPayload> buildUsageTree(PsiMethod method) {
        lock.lock();
        try {
            return buildUsageTreeInner(method, null, null);
        } finally {
            lock.unlock();
        }
    }

    public Node<TransactionInformationPayload> buildUsageTreeWithBenchmarking(PsiMethod method) {
        lock.lock();
        try {
            LocalDateTime before = LocalDateTime.now();
            AtomicInteger methodCounter = new AtomicInteger(0);
            Node<TransactionInformationPayload> out = buildUsageTreeInner(method, methodCounter, null);
            LocalDateTime after = LocalDateTime.now();
            LOG.info("Millisecs to run recursion = " + ChronoUnit.MILLIS.between(before, after));
            LOG.info("Methods counter = " + methodCounter.get());
            return out;
        } finally {
            lock.unlock();
        }
    }

    private Node<TransactionInformationPayload> buildUsageTreeInner(
            PsiMethod method, AtomicInteger methodCounter, Node<TransactionInformationPayload> rootNode) {
        String classMethodName = PsiMethodUtils.getUniqueClassMethodName(method);
        if (cache.containsKey(classMethodName)) {
            return cache.get(classMethodName);
        }
        Node<TransactionInformationPayload> newNode = new Node<>(buildTransactionInformationPayload(method), rootNode);
        Optional.ofNullable(rootNode).ifPresent(t -> t.addChild(newNode));
        cache.put(classMethodName, newNode);

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
        payload.setClassName(PsiMethodUtils.getClassName(method));
        payload.setMethodIdentifier(PsiMethodUtils.getClassLevelUniqueMethodName(method));
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
