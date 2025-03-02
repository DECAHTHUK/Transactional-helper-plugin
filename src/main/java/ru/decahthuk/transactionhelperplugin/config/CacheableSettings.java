package ru.decahthuk.transactionhelperplugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Service
@State(
        name = "CacheableSettings",
        storages = @Storage("TransactionalHelperPluginSettings.xml")
)
@Getter
@Setter
public final class CacheableSettings implements PersistentStateComponent<CacheableSettings> {

    private int maxTreeDepth = 30;

    @Override
    public CacheableSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CacheableSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
