package ru.decahthuk.transactionhelperplugin.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FetchType {

    LAZY("LAZY"),
    EAGER("EAGER");

    private final String textValue;

    public static FetchType fromString(String textValue) {
        String[] parts = textValue.split("\\.");
        String value = parts.length > 1 ? parts[1] : parts[0]; // with and without specifying static import
        for (FetchType fetchType : FetchType.values()) {
            if (fetchType.getTextValue().equals(value)) {
                return fetchType;
            }
        }
        return EAGER;
    }
}
