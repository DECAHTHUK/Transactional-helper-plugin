package ru.decahthuk.transactionhelperplugin.model;

import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.SmartPsiElementPointer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import ru.decahthuk.transactionhelperplugin.model.enums.TransactionalPropagation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Smart pointer to a method. Used in UI tree build process
     *
     * @see ru.decahthuk.transactionhelperplugin.toolWindow.tree.TransactionalTreeBuilder#buildTree(Node)
     */
    private SmartPsiElementPointer<PsiMethod> psiMethodPointer;

    /**
     * Transactional params
     */
    private TransactionalPropagation propagation;

    /**
     * Information about where this method is invoked as a lambda reference(there may be multiple different invocations in same method)
     * key - containing method
     * value - list of all lambda invocations
     */
    private Map<String, List<LambdaReferenceInformation>> lambdaReferenceInformationMap = new HashMap<>();

    /**
     * Information about how many times was method invoked from some method (needed for lambda counter)
     * key - containing method
     * value - number of invocations
     */
    private Map<String, Integer> numberOfCallsInsideOfMethod = new HashMap<>();

    /**
     * List of method names where incorrect self invocation(no self injection) is present
     */
    private List<String> incorrectSelfInvocationsContainingMethodsList = new ArrayList<>();

    public Navigatable getNavigatable() {
        return Optional.ofNullable(getPsiMethodPointer().getElement())
                .map(t -> (Navigatable) t.getNavigationElement()).orElse(null);
    }

    public boolean containsLambdaReferencesFromMethod(String methodIdentifier) {
        return lambdaReferenceInformationMap.containsKey(methodIdentifier);
    }

    public boolean anyLambdaReferenceIsOfPropagation(String methodIdentifier, TransactionalPropagation propagation) {
        List<LambdaReferenceInformation> lambdas = lambdaReferenceInformationMap.get(methodIdentifier);
        if (CollectionUtils.isNotEmpty(lambdas)) {
            for (LambdaReferenceInformation lambda : lambdas) {
                if (lambda.getPropagation() == propagation) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean anyLambdaReferenceIsNotTransactional(String methodIdentifier) {
        List<LambdaReferenceInformation> lambdas = lambdaReferenceInformationMap.get(methodIdentifier);
        if (CollectionUtils.isNotEmpty(lambdas)) {
            if (lambdas.size() != numberOfCallsInsideOfMethod.get(methodIdentifier)) {
                return true;
            }
            for (LambdaReferenceInformation lambda : lambdas) {
                if (!lambda.isTransactional()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean anyLambdaReferenceIsTransactional(String methodIdentifier) {
        List<LambdaReferenceInformation> lambdas = lambdaReferenceInformationMap.get(methodIdentifier);
        if (CollectionUtils.isNotEmpty(lambdas)) {
            for (LambdaReferenceInformation lambda : lambdas) {
                if (lambda.isTransactional()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean anyLambdaReferenceNotInListOfPropagations(String methodIdentifier, List<TransactionalPropagation> propagations) {
        List<LambdaReferenceInformation> lambdas = lambdaReferenceInformationMap.get(methodIdentifier);
        if (CollectionUtils.isNotEmpty(lambdas)) {
            for (LambdaReferenceInformation lambda : lambdas) {
                if (lambda.isTransactional() && !propagations.contains(lambda.getPropagation())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean noneLambdaReferencesInListOfPropagations(String methodIdentifier, List<TransactionalPropagation> propagations) {
        List<LambdaReferenceInformation> lambdas = lambdaReferenceInformationMap.get(methodIdentifier);
        if (CollectionUtils.isNotEmpty(lambdas)) {
            for (LambdaReferenceInformation lambda : lambdas) {
                if (lambda.isTransactional() && propagations.contains(lambda.getPropagation())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean allLambdaReferencesIsOfPropagation(String methodIdentifier, TransactionalPropagation propagation) {
        List<LambdaReferenceInformation> lambdas = lambdaReferenceInformationMap.get(methodIdentifier);
        if (CollectionUtils.isNotEmpty(lambdas)) {
            for (LambdaReferenceInformation lambda : lambdas) {
                if (lambda.getPropagation() != propagation) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean allCallsAreLambdaReferences(String methodIdentifier) {
        List<LambdaReferenceInformation> lambdas = lambdaReferenceInformationMap.get(methodIdentifier);
        if (CollectionUtils.isNotEmpty(lambdas)) {
            return lambdas.size() == numberOfCallsInsideOfMethod.get(methodIdentifier);
        }
        return false;
    }

    public void addCall(String methodIdentifier) {
        if (methodIdentifier != null) {
            numberOfCallsInsideOfMethod.compute(methodIdentifier, (k, v) -> v == null ? 1 : v + 1);
        }
    }

    public void addLambdaReference(String containingMethodName, LambdaReferenceInformation lambdaReferenceInformation) {
        if (lambdaReferenceInformation != null) {
            lambdaReferenceInformationMap.compute(containingMethodName,
                    (k, v) -> v == null ? new ArrayList<>() : v).add(lambdaReferenceInformation);
        }
    }

    public void addIncorrectSelfInvocation(String containingMethod) {
        incorrectSelfInvocationsContainingMethodsList.add(containingMethod);
    }

    public boolean methodIsCorrectlySelfInvokedFromMethod(TransactionInformationPayload payload) {
        return !incorrectSelfInvocationsContainingMethodsList.contains(payload.getMethodIdentifier());
    }

    public boolean methodIsCorrectlySelfInvokedFromMethod(String methodIdentifier) {
        return !incorrectSelfInvocationsContainingMethodsList.contains(methodIdentifier);
    }
}
