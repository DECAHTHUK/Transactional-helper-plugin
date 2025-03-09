package ru.decahthuk.transactionhelperplugin.toolWindow.legend;

import com.intellij.openapi.util.IconLoader;
import ru.decahthuk.transactionhelperplugin.bundle.UIBundle;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.style.IconPaths;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class LegendPanel extends JPanel {

    public LegendPanel() {
        setLayout(new GridLayout(0, 2));
        setBorder(BorderFactory.createTitledBorder(UIBundle.message("toolWindow.legend.window-name")));
        initLegend();
    }

    private void initLegend() {
        String selfInitPath = IconPaths.ICON_PATH + IconPaths.SELF_INIT_SUB_PATH;
        String lambdaRefPath = IconPaths.ICON_PATH + IconPaths.LAMBDA_REF_SUB_PATH;
        String normalPath = IconPaths.ICON_PATH + IconPaths.NORMAL_SUB_PATH;

        addLegendItem(loadIcon(normalPath + IconPaths.NONE_SUB_PATH), UIBundle.message("toolWindow.legend.no-transaction"));
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.REQUIRED.getIconPath()), UIBundle.message("toolWindow.legend.required-propagation"));
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.SUPPORTS.getIconPath()), UIBundle.message("toolWindow.legend.supports-propagation"));
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.MANDATORY.getIconPath()), UIBundle.message("toolWindow.legend.mandatory-propagation"));
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.REQUIRES_NEW.getIconPath()), UIBundle.message("toolWindow.legend.requires-new-propagation"));
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.NOT_SUPPORTED.getIconPath()), UIBundle.message("toolWindow.legend.not-supported-propagation"));
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.NEVER.getIconPath()), UIBundle.message("toolWindow.legend.never-propagation"));
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.NESTED.getIconPath()), UIBundle.message("toolWindow.legend.nested-propagation"));
        addLegendItem(loadIcon(selfInitPath + IconPaths.NONE_SUB_PATH), UIBundle.message("toolWindow.legend.self-init-problem"));
        addLegendItem(loadIcon(lambdaRefPath + IconPaths.NONE_SUB_PATH), UIBundle.message("toolWindow.legend.lambda-ref-problem"));
    }

    private void addLegendItem(Icon icon, String description) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        itemPanel.add(new JLabel(icon));
        itemPanel.add(new JLabel(description));
        add(itemPanel);
    }

    private Icon loadIcon(String calculatedPath) {
        return IconLoader.getIcon(calculatedPath, LegendPanel.class);
    }
}
