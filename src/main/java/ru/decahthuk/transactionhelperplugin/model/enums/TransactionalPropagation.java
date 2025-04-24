package ru.decahthuk.transactionhelperplugin.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.style.IconPaths;

@Getter
@RequiredArgsConstructor
public enum TransactionalPropagation {
    REQUIRED("REQUIRED", IconPaths.REQUIRED_SUB_PATH), // If transaction exists then supports, else creates new
    SUPPORTS("SUPPORTS", IconPaths.SUPPORTS_SUB_PATH), // If transaction exists then supports, else no transaction
    MANDATORY("MANDATORY", IconPaths.MANDATORY_SUB_PATH), // If transaction exists then supports, else throws exception
    REQUIRES_NEW("REQUIRES_NEW", IconPaths.REQUIRES_NEW_SUB_PATH), // If transaction exists then suspends it and creates new, else creates new
    NOT_SUPPORTED("NOT_SUPPORTED", IconPaths.NOT_SUPPORTED_SUB_PATH), // If transaction exists then suspends it
    NEVER("NEVER", IconPaths.NEVER_SUB_PATH), // If transaction exists then throws exception
    NESTED("NESTED", IconPaths.NESTED_SUB_PATH); // If transaction exists then creates save point, else creates new

    private final String textValue;
    private final String iconPath;

    public static TransactionalPropagation fromString(String textValue) {
        String[] parts = textValue.split("\\.");
        String value = parts.length > 1 ? parts[1] : parts[0]; // with and without specifying static import
        for (TransactionalPropagation propagation : TransactionalPropagation.values()) {
            if (propagation.getTextValue().equals(value)) {
                return propagation;
            }
        }
        return null;
    }
}
