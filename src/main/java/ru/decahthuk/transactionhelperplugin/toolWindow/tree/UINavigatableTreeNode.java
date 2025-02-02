package ru.decahthuk.transactionhelperplugin.toolWindow.tree;

import com.intellij.pom.Navigatable;
import lombok.Getter;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;

import javax.swing.tree.DefaultMutableTreeNode;

public class UINavigatableTreeNode extends DefaultMutableTreeNode implements Navigatable {

    private final Navigatable navigatable;
    @Getter
    private final TransactionInformationPayload payload;

    public UINavigatableTreeNode(String label, Navigatable navigatable, TransactionInformationPayload payload) {
        super(label);
        this.navigatable = navigatable;
        this.payload = payload;
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
