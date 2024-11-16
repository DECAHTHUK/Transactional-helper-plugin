package ru.decahthuk.transactionhelperplugin.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionalPropagation {
    REQUIRED("Propagation.REQUIRED"), // If transaction exists then supports, else creates new
    SUPPORTS("Propagation.SUPPORTS"), // If transaction exists then supports, else no transaction
    MANDATORY("Propagation.MANDATORY"), // If transaction exists then supports, else throws exception
    REQUIRES_NEW("Propagation.REQUIRES_NEW"), // If transaction exists then suspends it and creates new, else creates new
    NOT_SUPPORTED("Propagation.NOT_SUPPORTED"), // If transaction exists then suspends it
    NEVER("Propagation.NEVER"), // If transaction exists then throws exception
    NESTED("Propagation.NESTED"); // If transaction exists then creates save point, else creates new

    private final String textValue;

    public static TransactionalPropagation fromString(String textValue) {
        for (TransactionalPropagation propagation : TransactionalPropagation.values()) {
            if (propagation.getTextValue().equals(textValue)) {
                return propagation;
            }
        }
        return null;
    }
}
