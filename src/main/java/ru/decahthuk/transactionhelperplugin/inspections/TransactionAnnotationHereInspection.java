package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.service.EntitySearcherService;
import ru.decahthuk.transactionhelperplugin.service.TransactionSearcherService;

import java.util.Objects;

import static ru.decahthuk.transactionhelperplugin.utils.Constants.TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME;

public class TransactionAnnotationHereInspection extends AbstractBaseJavaLocalInspectionTool {

    TransactionSearcherService transactionSearcherService =
            ApplicationManager.getApplication().getService(TransactionSearcherService.class);
    EntitySearcherService entitySearcherService =
            ApplicationManager.getApplication().getService(EntitySearcherService.class);


    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                super.visitMethod(method);
                PsiAnnotation[] annotations = method.getAnnotations();
                transactionSearcherService.buildUsageTreeWithBenchmarking(method);
                for (PsiAnnotation annotation : annotations) {
                    String annotationName = annotation.getQualifiedName();
                    if (Objects.equals(annotationName, TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME)) {
                        transactionSearcherService.buildUsageTreeWithBenchmarking(method);
                        entitySearcherService.findEntityClasses(method.getProject());
                    }
                }
            }
        };
    }
}
