package ru.decahthuk.transactionhelperplugin.toolWindow.tree;

import com.intellij.ui.treeStructure.SimpleTreeStructure;
import org.jetbrains.annotations.NotNull;

public class CallTreeModel extends SimpleTreeStructure {

    private final CallTreeNode root;

    public CallTreeModel(CallTreeNode root) {
        super();
        this.root = root;
    }

    @Override
    public @NotNull Object getRootElement() {
        return root;
    }
}