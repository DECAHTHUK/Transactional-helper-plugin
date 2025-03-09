package ru.decahthuk.transactionhelperplugin.utils;

import org.jetbrains.annotations.NonNls;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;

import java.util.List;

@NonNls
public final class Constants {

    public static final String AUTOWIRED_ANNOTATION_QUALIFIED_NAME = "org.springframework.beans.factory.annotation.Autowired";
    public static final String TRANSACTIONAL_ANNOTATION_QUALIFIED_NAME = "org.springframework.transaction.annotation.Transactional";
    public static final List<String> ENTITY_ANNOTATION_QUALIFIED_NAMES =
            List.of("jakarta.persistence.Entity", "javax.persistence.Entity");
    public static final String TEST_CLASS_POSTFIX = "Test";
    public static final String TRANSACTIONAL_PROPAGATION_ARG_NAME = "propagation";
    public static final String JOIN_TABLE_FETCH_TYPE_ARG_NAME = "fetch";
    public static final List<String> JOIN_TABLE_ANNOTATIONS = List.of("javax.persistence.OneToOne", "javax.persistence.OneToMany",
            "javax.persistence.ManyToOne", "javax.persistence.ManyToMany", "javax.persistence.ElementCollection", "jakarta.persistence.OneToOne",
            "jakarta.persistence.OneToMany", "jakarta.persistence.ManyToOne", "jakarta.persistence.ManyToMany", "jakarta.persistence.ElementCollection");
    public static final String GETTER_PREFIX = "get";
    public static final String SELF_WIRED_FIELD_NAME = "self";

    public static final TransactionalPropagation DEFAULT_PROPAGATION = TransactionalPropagation.REQUIRED;

    private Constants() {
    }
}
