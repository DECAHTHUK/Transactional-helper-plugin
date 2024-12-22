package ru.decahthuk.transactionhelperplugin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * Class name
     */
    private String className;

    /**
     * Method identifier(full qualified unique name)
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

    /**
     * Information about where this method is invoked as a lambda reference(there may be multiple different invocations in same method)
     * key - containing method
     * value - list of all lambda invocations
     */
    private Map<String, List<LambdaReferenceInformation>> lambdaReferenceInformationMap = new HashMap<>();

    /**
     * List of method names where incorrect self invocation(no self injection) is present
     */
    private List<String> incorrectSelfInvocationsContainingMethodsList = new ArrayList<>();

    public void addLambdaReference(LambdaReferenceInformation lambdaReferenceInformation) {
        if (lambdaReferenceInformation != null) {
            lambdaReferenceInformationMap.compute(lambdaReferenceInformation.getMethodIdentifier(),
                    (k, v) -> v == null ? new ArrayList<>() : v).add(lambdaReferenceInformation);
        }
    }

    public void addIncorrectSelfInvocation(String containingMethod) {
        incorrectSelfInvocationsContainingMethodsList.add(containingMethod);
    }

    public boolean isMethodIsIncorrectlySelfInvokedFromMethodWithName(String containingMethod) {
        return incorrectSelfInvocationsContainingMethodsList.contains(containingMethod);
    }

    public boolean hasProblemsWithSelfInvocation() {
        return !incorrectSelfInvocationsContainingMethodsList.isEmpty();
    }
}
