/* TODO : PropagationQuickFix does not pop up in getAllQuickFixes
package ru.decahthuk.transactionhelperplugin.inspections.quickfix;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;
import ru.decahthuk.transactionhelperplugin.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.inspections.BaseTransactionalInspectionTest;
import ru.decahthuk.transactionhelperplugin.inspections.NeverPropagationInspection;

public class NeverPropagationQuickFixTest extends BaseTransactionalInspectionTest {

    private static final String FIX_TEXT = InspectionBundle.message("inspection.transaction.any-propagation.quick-fix.name");

    @Override
    protected String getTestDataPath() {
        return "testData/inspections/neverPropagation/quickFix";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new NeverPropagationInspection();
    }

    public void testNeverPropagationHasNoProblems() {
        assertThrows(IllegalStateException.class, "No quick fix found",
                () -> runBasicQuickFixTest("NeverPropagationWithNoProblems", FIX_TEXT));
    }

    public void testNeverPropagationHasProblem() {
        runBasicQuickFixTest("NeverPropagationWithProblem", FIX_TEXT);
    }
}
*/
