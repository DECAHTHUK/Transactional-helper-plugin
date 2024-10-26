package ru.decahthuk.transactionhelperplugin.model;

import com.intellij.psi.PsiMethod;
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
     * Method
     */
    private PsiMethod psiMethod; // TODO: Может это не нужно и можно будет сократить до имени или какого-нибудь референса

    /**
     * Is transaction declared
     */
    private boolean isTransactional; // TODO: Не забыть проверить class level аннотации + лямбда референсы

    /**
     * Transactional params
     */
    private Map<String, String> args;
}
