package ru.decahthuk.transactionhelperplugin.toolWindow;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.pom.Navigatable;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.ui.ColoredSideBorder;
import com.intellij.ui.JBColor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.decahthuk.transactionhelperplugin.bundle.UIBundle;
import ru.decahthuk.transactionhelperplugin.config.CacheableSettings;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.service.TransactionalSearcherService;
import ru.decahthuk.transactionhelperplugin.utils.PsiMethodUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PluginSettingsToolWindow {

    private JButton refreshInspectionsButton;
    private JPanel content;
    private JComboBox<MethodData> classMethodsBox;
    private JButton goToButton;
    private JButton viewTreeButton;
    private JButton refreshLayoutButton;
    private JTextField maxTreeDepthTextField;
    private JButton saveMaxTreeDepthButton;
    private JLabel maxTreeDepthErrorLabel;
    private JCheckBox osivModeCheckBox;

    public PluginSettingsToolWindow(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DumbService.getInstance(project).runWhenSmart(() -> {
            fillComboBoxData(project);

            maxTreeDepthTextField.setText(String.valueOf(project.getService(CacheableSettings.class).getMaxTreeDepth()));
            refreshLayoutButton.addActionListener(e -> fillComboBoxData(project));
            goToButton.addActionListener(e -> {
                MethodData methodData = (MethodData) classMethodsBox.getSelectedItem();
                if (methodData != null) {
                    methodData.getNavigatable().navigate(true);
                }
            });
            saveMaxTreeDepthButton.addActionListener(e -> saveMaxTreeDepthButtonLogic(project));
            viewTreeButton.addActionListener(e -> {
                if (classMethodsBox.getSelectedItem() != null) {
                    showTreeWindow(project, (MethodData) classMethodsBox.getSelectedItem());
                }
            });
            refreshInspectionsButton.addActionListener(e -> {
                project.getService(TransactionalSearcherService.class).cacheEvict();
                DaemonCodeAnalyzer.getInstance(project).restart(); // rerunning inspections
            });

            osivModeCheckBox.setSelected(project.getService(CacheableSettings.class).isOSIVIsEnabled());
            osivModeCheckBox.addItemListener(osivCheckBoxItemListener(project));
        });
    }

    public @Nullable JComponent getContent() {
        return content;
    }

    private void showTreeWindow(Project project, MethodData methodData) {
        Node<TransactionInformationPayload> transactionInfo = project.getService(TransactionalSearcherService.class)
                .buildUsageTree(methodData.getPsiMethodPointer().getElement());
        TransactionalInfoDialog dialog = new TransactionalInfoDialog(transactionInfo);
        dialog.show();
    }

    private void saveMaxTreeDepthButtonLogic(Project project) {
        String data = maxTreeDepthTextField.getText();
        if (!StringUtils.isNumeric(data)) {
            maxTreeDepthTextField.setBorder(new ColoredSideBorder(JBColor.RED, JBColor.RED, JBColor.RED, JBColor.RED, 1));
            maxTreeDepthErrorLabel.setText(UIBundle.message("toolWindow.side-panel.max-tree-depth-validation-message"));
        } else {
            maxTreeDepthTextField.setBorder(null);
            maxTreeDepthErrorLabel.setText("");
            project.getService(CacheableSettings.class).setMaxTreeDepth(Integer.parseInt(data));
        }
    }

    private void fillComboBoxData(@NotNull Project project) {
        classMethodsBox.removeAllItems();

        VirtualFile[] virtualFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        for (VirtualFile virtualFile : virtualFiles) {
            if (virtualFile.getFileType() instanceof JavaFileType) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                if (psiFile instanceof PsiJavaFile) {
                    PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                    List<PsiMethod> methods = new ArrayList<>();

                    psiJavaFile.accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitClass(PsiClass aClass) {
                            if (aClass.getContainingClass() != null && aClass.hasModifierProperty(PsiModifier.STATIC)) {
                                return; // Skip inner static classes
                            }
                            Collections.addAll(methods, aClass.getMethods());
                            super.visitClass(aClass);
                        }
                    });
                    for (PsiMethod method : methods) {
                        MethodData methodData = new MethodData();
                        methodData.setPsiMethodPointer(SmartPointerManager.createPointer(method));
                        methodData.setMethodName(PsiMethodUtils.getClassLevelUniqueMethodName(method));

                        classMethodsBox.addItem(methodData);
                    }
                }
            }
        }
    }

    private ItemListener osivCheckBoxItemListener(@NotNull Project project) {
        return e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                project.getService(CacheableSettings.class).setOSIVIsEnabled(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                project.getService(CacheableSettings.class).setOSIVIsEnabled(false);
            }
            DaemonCodeAnalyzer.getInstance(project).restart(); // rerunning inspections
        };
    }

    @Data
    private static class MethodData {
        private SmartPsiElementPointer<PsiMethod> psiMethodPointer;
        private String methodName;

        @Override
        public String toString() {
            return methodName;
        }

        public Navigatable getNavigatable() {
            return Optional.ofNullable(getPsiMethodPointer().getElement())
                    .map(t -> (Navigatable) t.getNavigationElement()).orElse(null);
        }
    }
}
