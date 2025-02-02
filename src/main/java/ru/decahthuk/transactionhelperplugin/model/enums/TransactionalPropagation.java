package ru.decahthuk.transactionhelperplugin.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.decahthuk.transactionhelperplugin.toolWindow.tree.style.IconPaths;

@Getter
@RequiredArgsConstructor
public enum TransactionalPropagation {
    REQUIRED("Propagation.REQUIRED", IconPaths.REQUIRED_SUB_PATH), // If transaction exists then supports, else creates new
    SUPPORTS("Propagation.SUPPORTS", IconPaths.SUPPORTS_SUB_PATH), // If transaction exists then supports, else no transaction
    MANDATORY("Propagation.MANDATORY", IconPaths.MANDATORY_SUB_PATH), // If transaction exists then supports, else throws exception
    REQUIRES_NEW("Propagation.REQUIRES_NEW", IconPaths.REQUIRES_NEW_SUB_PATH), // If transaction exists then suspends it and creates new, else creates new
    NOT_SUPPORTED("Propagation.NOT_SUPPORTED", IconPaths.NOT_SUPPORTED_SUB_PATH), // If transaction exists then suspends it
    NEVER("Propagation.NEVER", IconPaths.NEVER_SUB_PATH), // If transaction exists then throws exception
    NESTED("Propagation.NESTED", IconPaths.NESTED_SUB_PATH); // If transaction exists then creates save point, else creates new

    private final String textValue;
    private final String iconPath;

    public static TransactionalPropagation fromString(String textValue) {
        for (TransactionalPropagation propagation : TransactionalPropagation.values()) {
            if (propagation.getTextValue().equals(textValue)) {
                return propagation;
            }
        }
        return null;
    }
}
