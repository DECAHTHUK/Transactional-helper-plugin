package ru.decahthuk.transactionhelperplugin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Transaction info
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionInformationPayload {

    /**
     * Название класса
     */
    private String className;

    /**
     * Идентификатор метода
     */
    private String methodIdentifier;

    /**
     * Is transaction declared
     */
    private boolean isTransactional; // TODO: Не забыть проверить лямбда референсы

    /**
     * Transactional params
     */
    private Map<String, String> args;
}
