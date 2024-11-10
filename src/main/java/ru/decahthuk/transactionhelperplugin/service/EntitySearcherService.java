package ru.decahthuk.transactionhelperplugin.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public final class EntitySearcherService {

    private final List<PsiClass> entityClasses = new ArrayList<>();

    public List<PsiClass> findEntityClasses(@NotNull Project project) {
        ApplicationManager.getApplication().runReadAction(() -> {
            PsiClass annotationClass = JavaPsiFacade.getInstance(project)
                    .findClass("jakarta.persistence.Entity", GlobalSearchScope.allScope(project));

            if (annotationClass == null) {
                System.out.println("There is no jakarta persistence dependency"); // TODO: add old dependency support
                entityClasses.clear(); // TODO: Cache evict call
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
}
