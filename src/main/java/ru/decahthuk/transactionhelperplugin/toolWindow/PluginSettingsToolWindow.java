package ru.decahthuk.transactionhelperplugin.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PluginSettingsToolWindow implements ToolWindowFactory {
    private JCheckBox lazyInitializationInspectionCheckBox;
    private JCheckBox mandatoryPropagationInspectionCheckBox;
    private JCheckBox neverPropagationInspectionCheckBox;
    private JCheckBox potentiallyUnwantedNestedREQUIRES_NEWCheckBox;
    private JCheckBox transactionalSelfInvocationInspectionCheckBox;
    private JButton refreshInspectionsButton;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

    }

    private static class PluginSettingsToolWindowContent {

    }
}
