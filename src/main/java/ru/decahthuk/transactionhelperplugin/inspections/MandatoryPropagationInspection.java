package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.bundle.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.inspections.quickFix.PropagationQuickFix;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;
import ru.decahthuk.transactionhelperplugin.service.TransactionalSearcherService;
import ru.decahthuk.transactionhelperplugin.service.utils.TransactionalTreeAnalyzer;
import ru.decahthuk.transactionhelperplugin.utils.PsiAnnotationUtils;

import java.util.List;

public class MandatoryPropagationInspection extends AbstractBaseJavaLocalInspectionTool {

    @NonNls
    private static final Logger LOG = Logger.getInstance(MandatoryPropagationInspection.class);

    private TransactionalSearcherService transactionalSearcherService;

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        Project project = holder.getProject();
        transactionalSearcherService = project.getService(TransactionalSearcherService.class);
        return new JavaElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                super.visitMethod(method);
                List<PsiAnnotation> annotations = PsiAnnotationUtils.getMethodLevelAndClassLevelAnnotations(method);
                for (PsiAnnotation annotation : annotations) {
                    if (PsiAnnotationUtils.annotationIsTransactionalWithPropagation(annotation, TransactionalPropagation.MANDATORY)) {
                        Node<TransactionInformationPayload> tree = transactionalSearcherService.buildUsageTreeWithBenchmarking(method);
                        if (Boolean.TRUE.equals(TransactionalTreeAnalyzer
                                .treeBranchContainsNoTransactionWithoutCurrent(tree))) {
                            LOG.debug("MandatoryPropagationInspection ping");
                            holder.registerProblem(annotation,
                                    InspectionBundle.message("inspection.transaction.mandatory.descriptor"),
                                    new PropagationQuickFix());
                        }
                    }
                }
            }
        };
    }

    @Override
    public @NonNls @NotNull String getShortName() {
        return shortName();
    }

    public static String shortName() {
        return InspectionBundle.message("inspection.transaction.mandatory.short.name");
    }
}
