package ru.decahthuk.transactionhelperplugin.inspections.quickFix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.bundle.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.utils.Constants;

public class TransactionalSelfInvocationQuickFix implements LocalQuickFix {

    @NonNls
    private static final Logger LOG = Logger.getInstance(TransactionalSelfInvocationQuickFix.class);

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
        return InspectionBundle.message("inspection.method.transactional.self.invocation.quick-fix.name");
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiElement element = descriptor.getPsiElement();
        if (element instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression call = (PsiMethodCallExpression) element;
            PsiClass containingClass = PsiTreeUtil.getParentOfType(call, PsiClass.class);
            if (containingClass != null) {
                PsiMethod method = call.resolveMethod();
                if (method != null) {
                    String classQualifiedName = containingClass.getQualifiedName();
                    if (classQualifiedName != null) {
                        PsiType fieldType = PsiType.getTypeByName(classQualifiedName, project, containingClass.getResolveScope());
                        PsiField field = findSelfClass(containingClass, fieldType);
                        WriteCommandAction.runWriteCommandAction(project, () -> {
                            if (field == null) {
                                PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
                                PsiField fieldCreated = elementFactory.createField(Constants.SELF_WIRED_FIELD_NAME, fieldType);

                                PsiAnnotation autowiredAnnotation = elementFactory.createAnnotationFromText(
                                        "@" + Constants.AUTOWIRED_ANNOTATION_QUALIFIED_NAME, containingClass);
                                fieldCreated.getModifierList().addAfter(autowiredAnnotation, null);

                                containingClass.add(fieldCreated);
                            }

                            String fieldNameUsed = field == null ? Constants.SELF_WIRED_FIELD_NAME : field.getName();

                            String methodReferenceText = call.getMethodExpression().getParent().getText();
                            PsiExpression newExpression = JavaPsiFacade.getElementFactory(project).createExpressionFromText(
                                    fieldNameUsed + "." + methodReferenceText, call);
                            call.replace(newExpression);

                            JavaCodeStyleManager.getInstance(project).shortenClassReferences(containingClass);
                        });
                    } else {
                        LOG.error("containingClass qualified name is null");
                    }
                } else {
                    LOG.error("method is null");
                }
            } else {
                LOG.error("containingClass is null");
            }
        } else {
            LOG.error("Element is of an unsupported type: " + element.getClass());
        }
    }

    private PsiField findSelfClass(PsiClass psiClass, PsiType fieldType) {
        for (PsiField field : psiClass.getFields()) {
            if (field.getType().equals(fieldType)) {
                return field;
            }
        }
        return null;
    }
}
