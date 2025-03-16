package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.bundle.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.config.CacheableSettings;
import ru.decahthuk.transactionhelperplugin.model.EntityClassInformation;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.service.EntitySearcherService;
import ru.decahthuk.transactionhelperplugin.service.staticservice.TransactionalMethodAnalyzer;
import ru.decahthuk.transactionhelperplugin.service.TransactionalSearcherService;
import ru.decahthuk.transactionhelperplugin.service.staticservice.TransactionalTreeAnalyzer;
import ru.decahthuk.transactionhelperplugin.utils.Constants;

import java.util.Map;

public class LazyInitializationInspection extends AbstractBaseJavaLocalInspectionTool {

    @NonNls
    private static final Logger LOG = Logger.getInstance(LazyInitializationInspection.class);

    private TransactionalSearcherService transactionalSearcherService;
    private EntitySearcherService entitySearcherService;
    private CacheableSettings cacheableSettings;

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        Project project = holder.getProject();
        transactionalSearcherService = project.getService(TransactionalSearcherService.class);
        entitySearcherService = project.getService(EntitySearcherService.class);
        cacheableSettings = project.getService(CacheableSettings.class);
        if (entitySearcherService.jakartaIsNotPresent()) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }
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
                        PsiMethod method = PsiTreeUtil.getParentOfType(call, PsiMethod.class);
                        Node<TransactionInformationPayload> transactionInformationPayload =
                                transactionalSearcherService.buildUsageTreeWithBenchmarking(method);
                        if (Boolean.TRUE.equals(TransactionalTreeAnalyzer
                                .treeBranchContainsNoSessionWithCurrent(transactionInformationPayload, cacheableSettings.isOSIVIsEnabled()))) {
                            LOG.warn("LazyInitializationInspection ping");
                            holder.registerProblem(call,
                                    InspectionBundle.message("inspection.method.lazy.getter.call.descriptor"));
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
        return InspectionBundle.message("inspection.method.lazy.getter.call.short.name");
    }
}
