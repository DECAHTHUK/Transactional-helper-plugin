package ru.decahthuk.transactionhelperplugin.toolWindow.tree;

import com.intellij.ui.treeStructure.SimpleNode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Getter
public class CallTreeNode implements TreeNode {

    private final String label;
    private final String mark;
    private final List<CallTreeNode> children = new ArrayList<>();
    @Setter
    private CallTreeNode parent = null;

    public CallTreeNode(String label, String mark) {
        super();
        this.label = label;
        this.mark = mark;
    }

    public void addChild(CallTreeNode child) {
        children.add(child);
        child.setParent(this);
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        if (childIndex >= children.size()) {
            return null;
        }
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        if (node instanceof CallTreeNode) {
            CallTreeNode callTreeNode = (CallTreeNode) node;
            for (int i = 0; i < callTreeNode.children.size(); i++) {
                if (callTreeNode.children.get(i) == this) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(children); // TODO: maybe change if called freq
    }
}
