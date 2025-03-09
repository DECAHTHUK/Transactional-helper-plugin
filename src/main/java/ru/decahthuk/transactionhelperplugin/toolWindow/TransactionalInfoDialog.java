package ru.decahthuk.transactionhelperplugin.toolWindow;

import com.intellij.openapi.ui.DialogWrapper;
import ru.decahthuk.transactionhelperplugin.bundle.UIBundle;
import ru.decahthuk.transactionhelperplugin.model.Node;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.toolWindow.legend.LegendPanel;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.TransactionalTreeBuilder;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.TransactionalTreePanel;

import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;

public class TransactionalInfoDialog extends DialogWrapper {

    private final TransactionalTreePanel transactionalTreePanel;
    private final LegendPanel legendPanel;

    public TransactionalInfoDialog(Node<TransactionInformationPayload> transactionInfo) {
        super(true);
        DefaultTreeModel treeModel = TransactionalTreeBuilder.buildTree(transactionInfo);
        this.transactionalTreePanel = new TransactionalTreePanel(treeModel);
        this.legendPanel = new LegendPanel();
        init();
        setTitle(UIBundle.message("toolWindow.transactional-tree-panel.window-name"));
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(transactionalTreePanel, BorderLayout.CENTER);
        mainPanel.add(legendPanel, BorderLayout.SOUTH);
        return mainPanel;
    }
}
