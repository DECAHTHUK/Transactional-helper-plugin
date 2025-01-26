package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.inspections.quickFix.TransactionalSelfInvocationQuickFix;
import ru.decahthuk.transactionhelperplugin.service.TransactionalMethodAnalyzer;

public class TransactionalSelfInvocationInspection extends AbstractBaseJavaLocalInspectionTool {

    private static final Logger LOG = Logger.getInstance(TransactionalSelfInvocationInspection.class);

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitMethodCallExpression(@NotNull PsiMethodCallExpression call) {
                super.visitMethodCallExpression(call);
                if (TransactionalMethodAnalyzer.methodCallExpressionIsIncorrectClassLevelInvocation(call)) {
                    PsiMethod calledMethod = call.resolveMethod();
                    PsiMethod methodThatCalls = TransactionalMethodAnalyzer.getEnclosingMethod(call);
                    if (TransactionalMethodAnalyzer.methodsAreTransactionalSelfInvoked(calledMethod, methodThatCalls)) {
                        LOG.warn("TransactionalSelfInvocationInspection ping");
                        holder.registerProblem(call,
                                InspectionBundle.message("inspection.method.transactional.self.invocation.descriptor"),
                                new TransactionalSelfInvocationQuickFix());
                    }
                }
            }
        };
    }

    @Override
    public @NonNls @NotNull String getShortName() {
        return InspectionBundle.message("inspection.method.transactional.self.invocation.short.name");
    }
}