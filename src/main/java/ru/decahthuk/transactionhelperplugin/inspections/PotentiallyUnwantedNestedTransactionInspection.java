package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagations;
import ru.decahthuk.transactionhelperplugin.service.TransactionSearcherService;

import java.util.Map;
import java.util.Objects;

import static ru.decahthuk.transactionhelperplugin.utils.AnnotationUtils.parseAnnotationArgs;
import static ru.decahthuk.transactionhelperplugin.utils.Constants.TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME;
import static ru.decahthuk.transactionhelperplugin.utils.Constants.TRANSACTIONAL_PROPAGATION_ARG_NAME;

public class PotentiallyUnwantedNestedTransactionInspection extends AbstractBaseJavaLocalInspectionTool {

    TransactionSearcherService transactionSearcherService =
            ApplicationManager.getApplication().getService(TransactionSearcherService.class);

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
                        Map<String, String> args = parseAnnotationArgs(annotation);
                        if (args.containsKey(TRANSACTIONAL_PROPAGATION_ARG_NAME)) {
                            if (args.get(TRANSACTIONAL_PROPAGATION_ARG_NAME)
                                    .equalsIgnoreCase(TransactionalPropagations.REQUIRES_NEW.getTextValue())) {
                                System.out.println("PotentiallyUnwantedNestedTransactionInspection ping");
                                holder.registerProblem(annotation,
                                        InspectionBundle.message("inspection.transaction.nested.transaction.descriptor"));

                            }
                        }
                    }
                }
            }
        };
    }
}
