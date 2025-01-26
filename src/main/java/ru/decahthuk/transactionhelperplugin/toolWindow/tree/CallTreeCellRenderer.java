package ru.decahthuk.transactionhelperplugin.toolWindow.tree;

import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;

public class CallTreeCellRenderer extends ColoredTreeCellRenderer {

    @Override
    public void customizeCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof CallTreeNode) {
            CallTreeNode node = (CallTreeNode) value;
            append(node.getLabel(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            setToolTipText(node.getMark());
        }
    }
}
