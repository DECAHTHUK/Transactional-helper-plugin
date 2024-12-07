package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.model.EntityClassInformation;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.service.TransactionalMethodAnalyzer;
import ru.decahthuk.transactionhelperplugin.service.EntitySearcherService;
import ru.decahthuk.transactionhelperplugin.service.TransactionalSearcherService;
import ru.decahthuk.transactionhelperplugin.service.TransactionalTreeAnalyzer;
import ru.decahthuk.transactionhelperplugin.utils.Constants;

import java.util.Map;

public class LazyInitializationInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final Logger LOG = Logger.getInstance(LazyInitializationInspection.class);

    private TransactionalSearcherService transactionalSearcherService;
    private EntitySearcherService entitySearcherService;

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        Project project = holder.getProject();
        transactionalSearcherService = project.getService(TransactionalSearcherService.class);
        entitySearcherService = project.getService(EntitySearcherService.class);
        return new JavaElementVisitor() {
            @Override
            public void visitMethodCallExpression(@NotNull PsiMethodCallExpression call) {
                super.visitMethodCallExpression(call);
                PsiReferenceExpression methodExpression = call.getMethodExpression();
                String methodName = methodExpression.getReferenceName();

                if (methodName != null && methodName.startsWith(Constants.GETTER_PREFIX)) {
                    Map<String, EntityClassInformation> entityClassInformationMap =
                            entitySearcherService.getEntityClassesInformation();
                    boolean lazyCall = TransactionalMethodAnalyzer.isCallALazyInitializedEntity(entityClassInformationMap, call);
                    if (lazyCall) {
                        PsiMethod method = call.resolveMethod();
                        Node<TransactionInformationPayload> transactionInformationPayload =
                                transactionalSearcherService.buildUsageTreeWithBenchmarking(method);
                        if (TransactionalTreeAnalyzer.treeBranchContainsNoTransaction(transactionInformationPayload)) {
                            LOG.warn("LazyInitializationInspection ping");
                            holder.registerProblem(call,
                                    InspectionBundle.message("inspection.method.lazy.getter.call.descriptor"));
                        }
                    }
                }
            }
        };
    }
}
