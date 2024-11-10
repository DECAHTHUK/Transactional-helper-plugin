package ru.decahthuk.transactionhelperplugin.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionalPropagations {
    REQUIRED("Propagation.REQUIRED"),
    SUPPORTS("Propagation.SUPPORTS"),
    MANDATORY("Propagation.MANDATORY"),
    REQUIRES_NEW("Propagation.REQUIRES_NEW"),
    NOT_SUPPORTED("Propagation.NOT_SUPPORTED"),
    NEVER("Propagation.NEVER"),
    NESTED("Propagation.NESTED");

    private final String textValue;
}
