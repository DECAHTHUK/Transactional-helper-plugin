package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
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

    private TransactionSearcherService transactionSearcherService;
    private EntitySearcherService entitySearcherService;


    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        Project project = holder.getProject();
        transactionSearcherService = project.getService(TransactionSearcherService.class);
        entitySearcherService = project.getService(EntitySearcherService.class);
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
