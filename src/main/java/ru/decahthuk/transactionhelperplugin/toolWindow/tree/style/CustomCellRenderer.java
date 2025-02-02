package ru.decahthuk.transactionhelperplugin.toolWindow.tree.style;


import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;
import ru.decahthuk.transactionhelperplugin.model.TransactionInformationPayload;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.UINavigatableTreeNode;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Optional;

public class CustomCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node instanceof UINavigatableTreeNode) {
                UINavigatableTreeNode navigatableNode = (UINavigatableTreeNode) node;
                append(navigatableNode.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                setIcon(getIconForNode(navigatableNode));
            }
        }
    }

    private Icon getIconForNode(UINavigatableTreeNode node) {
        String path = IconPaths.ICON_PATH;
        TransactionInformationPayload payload = node.getPayload();
        TransactionInformationPayload parentPayload = Optional.ofNullable((UINavigatableTreeNode) node.getParent())
                .map(UINavigatableTreeNode::getPayload).orElse(null);

        // Checking for anomalies
        if (parentPayload != null && parentPayload.isTransactional() &&
                !parentPayload.methodIsCorrectlySelfInvokedFromMethod(payload.getMethodIdentifier())) {
            path += IconPaths.SELF_INIT_SUB_PATH;
        } else if (parentPayload != null && parentPayload.anyLambdaReferenceIsTransactional(payload.getMethodIdentifier())) {
            path += IconPaths.LAMBDA_REF_SUB_PATH;
        } else {
            path += IconPaths.NORMAL_SUB_PATH;
        }

        // Checking if transactional
        if (payload.isTransactional()) {
            path += payload.getPropagation().getIconPath();
        } else {
            path += IconPaths.NONE_SUB_PATH;
        }
        return loadIcon(path);
    }

    private Icon loadIcon(String calculatedPath) {
        return IconLoader.getIcon(calculatedPath, CustomCellRenderer.class);
    }
}