package ru.decahthuk.transactionhelperplugin.toolWindow.tree;


import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Random;

public class CustomCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node instanceof UINavigatableTreeNode) {
                UINavigatableTreeNode navigatableNode = (UINavigatableTreeNode) node;
                append(navigatableNode.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                setIcon(getIconForNode(navigatableNode)); // Set icon based on node type
            }
        }
    }

    private Icon getIconForNode(UINavigatableTreeNode node) {
        return new Random().nextBoolean() ? TreeNodeIcons.CLASS_ICON : TreeNodeIcons.FILE_ICON;
    }
}