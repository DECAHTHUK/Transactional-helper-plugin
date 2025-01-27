package ru.decahthuk.transactionhelperplugin.toolWindow.tree;

import com.intellij.pom.Navigatable;
import lombok.experimental.UtilityClass;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TransactionalTreeBuilder {

    public DefaultTreeModel buildTree(List<Navigatable> elements) {
        UINavigatableTreeNode root = new UINavigatableTreeNode("Root", null);
        for (Navigatable element : elements) {
            UINavigatableTreeNode node = new UINavigatableTreeNode(UUID.randomUUID().toString(), element);
            root.add(node);
        }

        return new DefaultTreeModel(root);
    }
}