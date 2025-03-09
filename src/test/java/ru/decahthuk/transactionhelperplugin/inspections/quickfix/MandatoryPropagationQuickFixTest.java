/* TODO : PropagationQuickFix does not pop up in getAllQuickFixes
package ru.decahthuk.transactionhelperplugin.inspections.quickfix;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;
import ru.decahthuk.transactionhelperplugin.bundle.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.inspections.BaseTransactionalInspectionTest;
import ru.decahthuk.transactionhelperplugin.inspections.MandatoryPropagationInspection;
import ru.decahthuk.transactionhelperplugin.inspections.TransactionalSelfInvocationInspection;

public class MandatoryPropagationQuickFixTest extends BaseTransactionalInspectionTest {

    private static final String FIX_TEXT = InspectionBundle.message("inspection.transaction.any-propagation.quick-fix.name");

    @Override
    protected String getTestDataPath() {
        return "testData/inspections/mandatoryPropagation/quickFix";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new MandatoryPropagationInspection();
    }

    public void testMandatoryPropagationHasNoProblems() {
        assertThrows(IllegalStateException.class, "No quick fix found",
                () -> runBasicQuickFixTest("MandatoryPropagationWithNoProblems", FIX_TEXT));
    }

    public void testMandatoryPropagationHasProblem() {
        runBasicQuickFixTest("MandatoryPropagationWithProblem", FIX_TEXT);
    }

    public void testMandatoryPropagationHasProblem_withMultipleProblems() {
        runBasicQuickFixTest("MandatoryPropagationWithMultipleProblems", FIX_TEXT);
    }
}
*/
