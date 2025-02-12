package ru.decahthuk.transactionhelperplugin.toolWindow.legend;

import com.intellij.openapi.util.IconLoader;
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
        setBorder(BorderFactory.createTitledBorder("Legend"));
        initLegend();
    }

    private void initLegend() {
        String selfInitPath = IconPaths.ICON_PATH + IconPaths.SELF_INIT_SUB_PATH;
        String lambdaRefPath = IconPaths.ICON_PATH + IconPaths.LAMBDA_REF_SUB_PATH;
        String normalPath = IconPaths.ICON_PATH + IconPaths.NORMAL_SUB_PATH;
        addLegendItem(loadIcon(normalPath + IconPaths.NONE_SUB_PATH), "No transaction");
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.REQUIRED.getIconPath()), "REQUIRED propagation");
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.SUPPORTS.getIconPath()), "SUPPORTS propagation");
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.MANDATORY.getIconPath()), "MANDATORY propagation");
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.REQUIRES_NEW.getIconPath()), "REQUIRES_NEW propagation");
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.NOT_SUPPORTED.getIconPath()), "NOT_SUPPORTED propagation");
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.NEVER.getIconPath()), "NEVER propagation");
        addLegendItem(loadIcon(normalPath + TransactionalPropagation.NESTED.getIconPath()), "NESTED propagation");
        addLegendItem(loadIcon(selfInitPath + IconPaths.NONE_SUB_PATH), "Some invocations might have problems with transactional self-init");
        addLegendItem(loadIcon(lambdaRefPath + IconPaths.NONE_SUB_PATH), "Some invocations might be called as a lambda from transactional method");
    }

    private void addLegendItem(Icon icon, String description) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.add(new JLabel(icon));
        itemPanel.add(new JLabel(description));
        add(itemPanel);
    }

    private Icon loadIcon(String calculatedPath) {
        return IconLoader.getIcon(calculatedPath, LegendPanel.class);
    }
}
