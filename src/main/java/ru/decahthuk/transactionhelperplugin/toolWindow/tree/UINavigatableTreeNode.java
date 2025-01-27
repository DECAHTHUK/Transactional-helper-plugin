package ru.decahthuk.transactionhelperplugin.toolWindow.tree;

import com.intellij.pom.Navigatable;

import javax.swing.tree.DefaultMutableTreeNode;

public class UINavigatableTreeNode extends DefaultMutableTreeNode implements Navigatable {

    private final Navigatable navigatable;

    public UINavigatableTreeNode(String label, Navigatable navigatable) {
        super(label);
        this.navigatable = navigatable;
    }

    @Override
    public void navigate(boolean requestFocus) {
        navigatable.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return navigatable != null && navigatable.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return navigatable != null && navigatable.canNavigateToSource();
    }
}
