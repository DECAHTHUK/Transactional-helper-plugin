package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.util.messages.Topic;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.PluginDisposable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service(Service.Level.PROJECT)
public final class EntitySearcherService implements Disposable {

    private final Project project;

    private final List<PsiClass> entityClasses = new ArrayList<>();

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
                if (entityClasses.isEmpty()) {
                    return;
                }
                PsiElement element = event.getElement();
                if (element instanceof PsiClass psiClass) {
                    if (entityClasses.contains(psiClass)) {
                        cacheEvict();
                    }
                }
            }
        }, PluginDisposable.getInstance(project));
    }

    public List<PsiClass> findEntityClasses(@NotNull Project project) {
        ApplicationManager.getApplication().runReadAction(() -> {
            PsiClass annotationClass = JavaPsiFacade.getInstance(project)
                    .findClass("jakarta.persistence.Entity", GlobalSearchScope.allScope(project));

            if (annotationClass == null) {
                System.out.println("There is no jakarta persistence dependency"); // TODO: add old dependency support
                cacheEvict();
                return;
            }
            Collection<PsiClass> annotatedElements = AnnotatedElementsSearch.searchPsiClasses(
                    annotationClass,
                    GlobalSearchScope.allScope(project)
            ).findAll();
            for (PsiClass element : annotatedElements) {
                if (element != null) {
                    System.out.println("Found class with annotation: " + element.getQualifiedName());
                    entityClasses.add(element);
                }
            }
        });
        return null;
    }

    private void cacheEvict() {
        entityClasses.clear();
    }

    @Override
    public void dispose() {
        cacheEvict();
    }
}
