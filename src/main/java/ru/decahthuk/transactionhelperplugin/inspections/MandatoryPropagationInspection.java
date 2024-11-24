package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;
import ru.decahthuk.transactionhelperplugin.service.TransactionSearcherService;
import ru.decahthuk.transactionhelperplugin.service.TransactionalTreeAnalyzer;
import ru.decahthuk.transactionhelperplugin.utils.PsiAnnotationUtils;

public class MandatoryPropagationInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final Logger LOG = Logger.getInstance(MandatoryPropagationInspection.class);

    private TransactionSearcherService transactionSearcherService;

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        Project project = holder.getProject();
        transactionSearcherService = project.getService(TransactionSearcherService.class);
        return new JavaElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                super.visitMethod(method);
                PsiAnnotation[] annotations = method.getAnnotations();
                for (PsiAnnotation annotation : annotations) {
                    if (PsiAnnotationUtils.annotationIsTransactionalWithPropagation(annotation, TransactionalPropagation.MANDATORY)) {
                        Node<TransactionInformationPayload> tree = transactionSearcherService.buildUsageTreeWithBenchmarking(method);
                        if (TransactionalTreeAnalyzer.treeBranchContainsNoTransaction(tree)) {
                            LOG.warn("MandatoryPropagationInspection ping");
                            holder.registerProblem(annotation,
                                    InspectionBundle.message("inspection.transaction.mandatory.descriptor"));
                        }
                    }
                }
            }
        };
    }
}
