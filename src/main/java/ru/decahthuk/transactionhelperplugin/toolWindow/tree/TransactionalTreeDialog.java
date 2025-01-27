package ru.decahthuk.transactionhelperplugin.toolWindow.tree;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.pom.Navigatable;

import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

public class TransactionalTreeDialog extends DialogWrapper {

    private final TransactionalTreePanel transactionalTreePanel;

    public TransactionalTreeDialog(List<Navigatable> elements) {
        super(true);
        DefaultTreeModel treeModel = TransactionalTreeBuilder.buildTree(elements);
        this.transactionalTreePanel = new TransactionalTreePanel(treeModel);
        init();
        setTitle("Custom Tree Viewer");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return transactionalTreePanel;
    }
}
