package ru.decahthuk.transactionhelperplugin.utils;

import java.util.List;

public final class Constants {

    public static final String TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME = "org.springframework.transaction.annotation.Transactional";
    public static final List<String> ENTITY_ANNOTATION_QUALIFIED_NAMES =
            List.of("jakarta.persistence.Entity", "javax.persistence.Entity");

    private Constants() {
    }
}
