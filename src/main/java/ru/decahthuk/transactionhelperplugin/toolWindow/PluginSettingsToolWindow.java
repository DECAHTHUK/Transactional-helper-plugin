package ru.decahthuk.transactionhelperplugin.toolWindow;

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
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.ColoredSideBorder;
import com.intellij.ui.JBColor;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.CallTreeCellRenderer;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.CallTreeModel;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.CallTreeNode;
import ru.decahthuk.transactionhelperplugin.utils.PsiMethodUtils;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public PluginSettingsToolWindow(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DumbService.getInstance(project).runWhenSmart(() -> {
            fillComboBoxData(project);
            refreshLayoutButton.addActionListener(e -> {
                fillComboBoxData(project);
            });
            goToButton.addActionListener(e -> {
                MethodData methodData = (MethodData) classMethodsBox.getSelectedItem();
                if (methodData != null) {
                    methodData.getNavigatable().navigate(true);
                }
            });
            saveMaxTreeDepthButton.addActionListener(e -> {
                String data = maxTreeDepthTextField.getText();
                if (!StringUtils.isNumeric(data)) {
                    maxTreeDepthTextField.setBorder(new ColoredSideBorder(JBColor.RED, JBColor.RED, JBColor.RED, JBColor.RED, 1));
                    maxTreeDepthErrorLabel.setText("Field should be numeric");
                } else {
                    maxTreeDepthTextField.setBorder(null);
                    maxTreeDepthErrorLabel.setText("");
                }
            });
            viewTreeButton.addActionListener(e -> {
                showTreeWindow(project);
            });
        });
    }

    public @Nullable JComponent getContent() {
        return content;
    }

    private void showTreeWindow(Project project) {
        JFrame frame = new JFrame("Object Tree");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);


        // Create the root node
        CallTreeNode root = new CallTreeNode("Root Method", "Mark");

        // Create child nodes
        CallTreeNode child1 = new CallTreeNode("Method1", "Mark1");
        CallTreeNode child2 = new CallTreeNode("Method2", "Mark2");
        CallTreeNode child3 = new CallTreeNode("Method3", "Mark3");

        root.addChild(child1);
        root.addChild(child2);
        root.addChild(child3);

        Tree tree = new Tree(root);
        tree.setCellRenderer(new CallTreeCellRenderer());

        frame.add(new JScrollPane(tree));

        frame.setVisible(true);
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
                            Collections.addAll(methods, aClass.getMethods());
                            super.visitClass(aClass);
                        }
                    });
                    for (PsiMethod method : methods) {
                        MethodData methodData = new MethodData();
                        methodData.setNavigatable((Navigatable) method.getNavigationElement());
                        methodData.setMethodName(PsiMethodUtils.getClassLevelUniqueMethodName(method));

                        classMethodsBox.addItem(methodData);
                    }
                }
            }
        }
    }

    @Data
    private static class MethodData {
        private Navigatable navigatable;
        private String methodName;

        @Override
        public String toString() {
            return methodName;
        }
    }
}
