package ru.decahthuk.transactionhelperplugin.utils;

import com.github.weisj.jsvg.S;

import java.util.List;

public final class Constants {

    public static final String TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME = "org.springframework.transaction.annotation.Transactional";
    public static final List<String> ENTITY_ANNOTATION_QUALIFIED_NAMES =
            List.of("jakarta.persistence.Entity", "javax.persistence.Entity");
    public static final String TEST_CLASS_POSTFIX = "Test";
    public static final String TRANSACTIONAL_PROPAGATION_ARG_NAME = "propagation";

    private Constants() {
    }
}
