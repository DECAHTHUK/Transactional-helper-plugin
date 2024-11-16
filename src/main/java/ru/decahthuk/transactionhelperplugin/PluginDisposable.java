package ru.decahthuk.transactionhelperplugin;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * The service is intended to be used instead of a project/application as a parent disposable.
 */
@Service({Service.Level.APP, Service.Level.PROJECT})
public final class PluginDisposable implements Disposable {

    public static @NotNull Disposable getInstance() {
        return ApplicationManager.getApplication().getService(PluginDisposable.class);
    }

    public static @NotNull Disposable getInstance(@NotNull Project project) {
        return project.getService(PluginDisposable.class);
    }

    @Override
    public void dispose() {
    }
}
