package ru.decahthuk.transactionhelperplugin.toolWindow.tree;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.pom.Navigatable;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;

import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

public class TransactionalTreeDialog extends DialogWrapper {

    private final TransactionalTreePanel transactionalTreePanel;

    public TransactionalTreeDialog(Node<TransactionInformationPayload> transactionInfo) {
        super(true);
        DefaultTreeModel treeModel = TransactionalTreeBuilder.buildTree(transactionInfo);
        this.transactionalTreePanel = new TransactionalTreePanel(treeModel);
        init();
        setTitle("Custom Tree Viewer");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return transactionalTreePanel;
    }
}
