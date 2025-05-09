package ru.decahthuk.transactionhelperplugin.bundle;


import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public final class InspectionBundle extends DynamicBundle {

    @NonNls
    public static final String BUNDLE = "messages.InspectionBundle";

    private static final InspectionBundle INSTANCE = new InspectionBundle();

    private InspectionBundle() {
        super(BUNDLE);
    }

    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
                                      Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }
}