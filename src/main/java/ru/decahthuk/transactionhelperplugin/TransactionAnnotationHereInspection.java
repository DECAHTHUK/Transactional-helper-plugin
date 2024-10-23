package ru.decahthuk.transactionhelperplugin;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.service.TransactionSearcherService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionAnnotationHereInspection extends AbstractBaseJavaLocalInspectionTool {

    TransactionSearcherService transactionSearcherService =
            ApplicationManager.getApplication().getService(TransactionSearcherService.class);


    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                super.visitMethod(method);
                PsiAnnotation[] annotations = method.getAnnotations();
                for (PsiAnnotation annotation : annotations) {
                    String annotationName = annotation.getQualifiedName();
                    if (annotationName.equals("org.springframework.transaction.annotation.Transactional")) {
                        holder.registerProblem(annotation,
                                InspectionBundle.message("inspection.transaction.annotation.here.descriptor"));
                        LocalDateTime before = LocalDateTime.now();
                        AtomicInteger methodCounter = new AtomicInteger(0);
                        transactionSearcherService.buildUsageTree(method, 0, methodCounter);
                        LocalDateTime after = LocalDateTime.now();
                        System.out.println("Millisecs to run recursion = " + ChronoUnit.MILLIS.between(before, after));
                        System.out.println("Methods counter = " + methodCounter.get());
                    }
                }
            }
        };
    }
}
