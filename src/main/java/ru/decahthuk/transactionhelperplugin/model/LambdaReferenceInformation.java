package ru.decahthuk.transactionhelperplugin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Information about lambda call of a method
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LambdaReferenceInformation {

    /**
     * Method invoker identifier
     */
    private String methodIdentifier;

    /**
     * Is transaction declared
     */
    private boolean isTransactional;

    /**
     * Transactional params
     */
    private Map<String, String> args;
}
