package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.PluginDisposable;
import ru.decahthuk.transactionhelperplugin.model.EntityClassInformation;
import ru.decahthuk.transactionhelperplugin.model.enums.FetchType;
import ru.decahthuk.transactionhelperplugin.utils.PsiAnnotationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ru.decahthuk.transactionhelperplugin.utils.Constants.ENTITY_ANNOTATION_QUALIFIED_NAMES;
import static ru.decahthuk.transactionhelperplugin.utils.Constants.JOIN_TABLE_ANNOTATIONS;
import static ru.decahthuk.transactionhelperplugin.utils.Constants.JOIN_TABLE_FETCH_TYPE_ARG_NAME;

@Slf4j
@Service(Service.Level.PROJECT)
public final class EntitySearcherService implements Disposable {

    private static final Logger LOG = Logger.getInstance(EntitySearcherService.class);

    private final Project project;

    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean dataIsFresh = new AtomicBoolean(false);
    private final Map<String, EntityClassInformation> entityClassInformationMap = new HashMap<>();

    public EntitySearcherService(Project project) {
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
                if (entityClassInformationMap.isEmpty()) {
                    return;
                }
                PsiElement element = event.getFile();
                if (element instanceof PsiJavaFile) {
                    PsiJavaFile psiJavaFile = (PsiJavaFile) element;
                    if (cacheContainsModule(psiJavaFile.getPackageName())) {
                        cacheEvict();
                    }
                }
            }
        }, PluginDisposable.getInstance(project));
    }

    public Map<String, EntityClassInformation> getEntityClassesInformation() {
        if (dataIsFresh.get() && !entityClassInformationMap.isEmpty()) {
            return entityClassInformationMap;
        }
        lock.lock();
        try {
            if (dataIsFresh.get() && !entityClassInformationMap.isEmpty()) {
                return entityClassInformationMap;
            }
            List<PsiClass> entityClasses = findEntityClasses();
            if (entityClasses.isEmpty()) {
                return Collections.emptyMap();
            }
            for (PsiClass psiClass : entityClasses) {
                if (psiClass != null) {
                    psiClass.accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitField(@NotNull PsiField field) {
                            super.visitField(field);
                            PsiAnnotation[] annotations = field.getAnnotations();
                            for (PsiAnnotation annotation : annotations) {
                                String annotationName = annotation.getQualifiedName();
                                if (annotationName != null && JOIN_TABLE_ANNOTATIONS.contains(annotationName)) {
                                    cacheFieldInformation(field, annotation, psiClass.getQualifiedName());
                                }
                            }
                        }
                    });
                }
            }
            dataIsFresh.set(true);
        } finally {
            lock.unlock();
        }
        return entityClassInformationMap;
    }

    private void cacheFieldInformation(PsiField field, PsiAnnotation annotation, String className) {
        EntityClassInformation entityClassInformation = entityClassInformationMap.get(className);
        if (entityClassInformation == null) {
            entityClassInformation = new EntityClassInformation();
        }
        Map<String, String> annotationArgs = PsiAnnotationUtils.parseAnnotationArgs(annotation);
        if (annotationArgs != null && annotationArgs.containsKey(JOIN_TABLE_FETCH_TYPE_ARG_NAME)) {
            FetchType fetchType = FetchType.fromString(annotationArgs.get(JOIN_TABLE_FETCH_TYPE_ARG_NAME));
            if (fetchType == FetchType.LAZY) {
                entityClassInformation.addLazyField(field.getName());
            }
        }
        entityClassInformationMap.putIfAbsent(className, entityClassInformation);
    }

    private List<PsiClass> findEntityClasses() {
        List<PsiClass> psiClasses = new ArrayList<>();
        ApplicationManager.getApplication().runReadAction(() -> {
            PsiClass annotationClass = null;
            for (String name : ENTITY_ANNOTATION_QUALIFIED_NAMES) {
                if (annotationClass == null) {
                    annotationClass = JavaPsiFacade.getInstance(project).findClass(name, GlobalSearchScope.allScope(project));
                }
            }

            if (annotationClass == null) {
                LOG.warn("There is no javax/jakarta persistence dependency");
                cacheEvict();
                return;
            }
            Collection<PsiClass> annotatedElements = AnnotatedElementsSearch.searchPsiClasses(
                    annotationClass,
                    GlobalSearchScope.allScope(project)
            ).findAll();
            for (PsiClass element : annotatedElements) {
                if (element != null) {
                    LOG.info("Found class with annotation: " + element.getQualifiedName());
                    psiClasses.add(element);
                }
            }
        });
        return psiClasses;
    }

    private boolean cacheContainsModule(String moduleName) {
        for (String className : entityClassInformationMap.keySet()) {
            if (StringUtils.startsWith(className, moduleName)) {
                return true;
            }
        }
        return false;
    }

    private void cacheEvict() {
        entityClassInformationMap.clear();
        dataIsFresh.set(false);
    }

    @Override
    public void dispose() {
        cacheEvict();
    }
}
