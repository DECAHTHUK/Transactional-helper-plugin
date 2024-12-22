package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.PluginDisposable;
import ru.decahthuk.transactionhelperplugin.model.LambdaReferenceInformation;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.utils.PsiMethodUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ru.decahthuk.transactionhelperplugin.utils.Constants.TEST_CLASS_POSTFIX;
import static ru.decahthuk.transactionhelperplugin.utils.Constants.TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME;
import static ru.decahthuk.transactionhelperplugin.utils.PsiAnnotationUtils.parseAnnotationArgs;

@Slf4j
@Service(Service.Level.PROJECT)
public final class TransactionalSearcherService implements Disposable {

    private static final Logger LOG = Logger.getInstance(TransactionalSearcherService.class);

    private final Lock lock = new ReentrantLock();
    private final Map<String, Node<TransactionInformationPayload>> cache = new HashMap<>();

    public TransactionalSearcherService(Project project) {
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
            PsiMethod method, AtomicInteger methodCounter, Node<TransactionInformationPayload> parentNode) {
        String classMethodName = PsiMethodUtils.getUniqueClassMethodName(method);
        if (cache.containsKey(classMethodName)) {
            Node<TransactionInformationPayload> cachedValue = cache.get(classMethodName);
            Optional.ofNullable(parentNode).ifPresent(t -> t.addChild(cachedValue));
            return cachedValue;
        }
        Node<TransactionInformationPayload> newNode = new Node<>(buildTransactionInformationPayload(method), parentNode);
        Optional.ofNullable(parentNode).ifPresent(t -> t.addChild(newNode));
        cache.put(classMethodName, newNode);

        if (methodCounter != null) {
            methodCounter.incrementAndGet();
        }

        Query<PsiReference> query = ReferencesSearch.search(method);
        Set<PsiMethod> visitedMethods = new HashSet<>();

        query.forEach(reference -> {
            PsiElement element = reference.getElement();
            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            if (containingMethod != null) {
                PsiClass containingClass = containingMethod.getContainingClass();
                // Excluding test classes. May make mechanism better
                if (containingClass == null || !String.valueOf(containingClass.getQualifiedName()).endsWith(TEST_CLASS_POSTFIX)) {
                    checkLambdaReference(element).ifPresent(t -> Optional.ofNullable(parentNode)
                            .ifPresent(pN -> pN.getData().addLambdaReference(t)));
                    if (!isCorrectSelfInvocation(method, containingMethod, element)) {
                        Optional.ofNullable(parentNode).ifPresent(t -> t.getData()
                                .addIncorrectSelfInvocation(PsiMethodUtils.getUniqueClassMethodName(containingMethod)));
                    }
                    visitedMethods.add(containingMethod);
                    if (!visitedMethods.contains(containingMethod)) {
                        buildUsageTreeInner(containingMethod, methodCounter, newNode);
                    }
                }
            }
        });
        return newNode;
    }

    public static TransactionInformationPayload buildTransactionInformationPayload(PsiMethod method) {
        TransactionInformationPayload payload = new TransactionInformationPayload();
        Map<String, String> transactionalArgs = searchTransactionalData(method);
        payload.setClassName(PsiMethodUtils.getClassName(method));
        payload.setMethodIdentifier(PsiMethodUtils.getUniqueClassMethodName(method));
        payload.setTransactional(transactionalArgs != null);
        payload.setArgs(transactionalArgs);
        return payload;
    }

    private static boolean isCorrectSelfInvocation(PsiMethod calledMethod, PsiMethod containingMethod, PsiElement reference) {
        PsiClass calledMethodContainingClass = calledMethod.getContainingClass();
        PsiClass containingMethodContainingClass = containingMethod.getContainingClass();
        if (calledMethodContainingClass != null && Objects.equals(calledMethodContainingClass, containingMethodContainingClass)) {
            PsiMethodCallExpression call = PsiTreeUtil.getParentOfType(reference, PsiMethodCallExpression.class);
            if (call != null) {
                PsiExpression expression = call.getMethodExpression().getQualifierExpression();
                if (expression != null) {
                    PsiType psiType = expression.getType();
                    if (psiType instanceof PsiClassReferenceType psiClassReferenceType) {
                        PsiClass methodContainer = psiClassReferenceType.resolve();
                        if (methodContainer != null && methodContainer.getQualifiedName() != null) {
                            return Objects.equals(methodContainer.getQualifiedName(),
                                    containingMethod.getContainingClass().getQualifiedName());
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    private static Optional<LambdaReferenceInformation> checkLambdaReference(PsiElement reference) {
        PsiLambdaExpression lambdaExpression = PsiTreeUtil.getParentOfType(reference, PsiLambdaExpression.class);
        if (lambdaExpression != null) {
            PsiMethodCallExpression lambdaInvokerCall = PsiTreeUtil.getParentOfType(lambdaExpression, PsiMethodCallExpression.class);
            if (lambdaInvokerCall != null) {
                PsiMethod lambdaInvokerMethod = lambdaInvokerCall.resolveMethod();
                if (lambdaInvokerMethod != null) {
                    Map<String, String> transactionalArgs = searchTransactionalData(lambdaInvokerMethod);
                    return Optional.of(new LambdaReferenceInformation(
                            PsiMethodUtils.getUniqueClassMethodName(lambdaInvokerMethod),
                            transactionalArgs != null,
                            transactionalArgs
                    ));
                }
            }
        }
        return Optional.empty();
    }

    private static Map<String, String> searchTransactionalData(PsiMethod method) {
        PsiAnnotation annotation = getAnyLevelTransactionalAnnotation(method);
        return parseAnnotationArgs(annotation);
    }

    private static PsiAnnotation getAnyLevelTransactionalAnnotation(PsiMethod method) {
        PsiAnnotation annotation = method.getModifierList().findAnnotation(TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME);
        if (annotation == null) { // Searching less prior Class-level annotations
            PsiClass containingClass = method.getContainingClass();
            if (containingClass != null) {
                annotation = Optional.ofNullable(containingClass.getModifierList())
                        .map(t -> t.findAnnotation(TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME)).orElse(null);
            }
        }
        return annotation;
    }

    private void cacheEvict() {
        cache.clear();
    }

    @Override
    public void dispose() {
        cacheEvict();
    }
}
