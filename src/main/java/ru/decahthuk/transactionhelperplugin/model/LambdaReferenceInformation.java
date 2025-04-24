package ru.decahthuk.transactionhelperplugin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;

/**
 * Information about lambda call of a method
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LambdaReferenceInformation {

    /**
     * Method invoker identifier (lambda)
     */
    private String methodIdentifier;

    /**
     * Is transaction declared
     */
    private boolean isTransactional;

    /**
     * Propagation (or null if not exists)
     */
    private TransactionalPropagation propagation;
}
