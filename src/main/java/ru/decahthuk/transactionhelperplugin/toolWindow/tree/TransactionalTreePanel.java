package ru.decahthuk.transactionhelperplugin.toolWindow.tree;


import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import lombok.Getter;
import ru.decahthuk.transactionhelperplugin.bundle.UIBundle;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.style.CustomCellRenderer;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    showContextMenu(tree, e.getX(), e.getY());
                }
            }
        });

        tree.setCellRenderer(new CustomCellRenderer());
    }

    private static void showContextMenu(JTree tree, int x, int y) {
        TreePath path = tree.getSelectionPath();
        if (path == null) return;

        Object node = path.getLastPathComponent();
        if (node instanceof UINavigatableTreeNode) {
            UINavigatableTreeNode treeNode = (UINavigatableTreeNode) node;

            JPopupMenu contextMenu = new JPopupMenu();

            JMenuItem openItem = new JMenuItem(UIBundle.message("toolWindow.transactional-tree-panel.context-menu.navigate-to"));
            openItem.addActionListener(event -> {
                treeNode.navigate(true);
            });
            contextMenu.add(openItem);
            contextMenu.addSeparator();

            JMenu infoMenu = new JMenu(UIBundle.message("toolWindow.transactional-tree-panel.context-menu.extended-info"));
            infoMenu.add(new JLabel(UIBundle.message("toolWindow.transactional-tree-panel.context-menu.propagation")
                    + ": " + treeNode.getPayload().getPropagation()));
            infoMenu.addSeparator();
            infoMenu.add(new JLabel(UIBundle.message("toolWindow.transactional-tree-panel.context-menu.self-init-problems")
                    + ": " + treeNode.isHasSelfInitIssues()));
            infoMenu.addSeparator();
            infoMenu.add(new JLabel(UIBundle.message("toolWindow.transactional-tree-panel.context-menu.transactional-lambda-reference")
                    + ": " + treeNode.isHasTransactionalLambdaRef()));
            infoMenu.addSeparator();
            infoMenu.add(new JLabel(UIBundle.message("toolWindow.transactional-tree-panel.context-menu.full-qualified-method-name")
                    + ": " + treeNode.getPayload().getMethodIdentifier()));
            contextMenu.add(infoMenu);

            contextMenu.show(tree, x, y);
        }
    }
}
