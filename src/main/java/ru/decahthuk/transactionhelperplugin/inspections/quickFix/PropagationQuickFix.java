package ru.decahthuk.transactionhelperplugin.inspections.quickFix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameValuePair;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.utils.Constants;

public class PropagationQuickFix implements LocalQuickFix {

    private static final Logger LOG = Logger.getInstance(PropagationQuickFix.class);

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return InspectionBundle.message("inspection.transaction.any-propagation.quick-fix.name");
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement element = descriptor.getPsiElement();
        if (element instanceof PsiAnnotation) {
            PsiAnnotation annotation = (PsiAnnotation) element;
            PsiNameValuePair[] attributes = annotation.getParameterList().getAttributes();
            for (PsiNameValuePair attribute : attributes) {
                if (Constants.TRANSACTIONAL_PROPAGATION_ARG_NAME.equals(attribute.getName())) {
                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        attribute.delete();
                        if (annotation.getParameterList().getAttributes().length == 0) {
                            annotation.getParameterList().getFirstChild().delete(); // Left brace
                            annotation.getParameterList().getLastChild().delete(); // Right brace
                        }
                    });
                    break;
                }
            }
        } else {
            LOG.error("Element is of an unsupported type: " + element.getClass());
        }
    }
}
