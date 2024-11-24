package ru.decahthuk.transactionhelperplugin.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FetchType {

    LAZY("FetchType.LAZY"),
    EAGER("FetchType.EAGER");

    private final String textValue;

    public static FetchType fromString(String textValue) {
        for (FetchType fetchType : FetchType.values()) {
            if (fetchType.getTextValue().equals(textValue)) {
                return fetchType;
            }
        }
        return EAGER;
    }
}
