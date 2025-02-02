package ru.decahthuk.transactionhelperplugin.toolWindow.tree;


import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import lombok.Getter;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.style.CustomCellRenderer;

import javax.swing.JPanel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;

@Getter
public class TransactionalTreePanel extends JPanel {
    private final Tree tree;

    public TransactionalTreePanel(DefaultTreeModel treeModel) {
        this.tree = new Tree(treeModel);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JBScrollPane scrollPane = new JBScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);

        // Enable virtualization for large trees
        tree.setLargeModel(true);

        tree.addTreeSelectionListener(e -> {
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                Object node = path.getLastPathComponent();
                if (node instanceof UINavigatableTreeNode) {
                    ((UINavigatableTreeNode) node).navigate(true);
                }
            }
        });

        tree.setCellRenderer(new CustomCellRenderer());
    }

}
