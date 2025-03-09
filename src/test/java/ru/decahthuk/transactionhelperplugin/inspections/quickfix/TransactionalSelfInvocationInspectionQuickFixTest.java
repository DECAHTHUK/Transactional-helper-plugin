package ru.decahthuk.transactionhelperplugin.inspections.quickfix;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;
import ru.decahthuk.transactionhelperplugin.bundle.InspectionBundle;
import ru.decahthuk.transactionhelperplugin.inspections.BaseTransactionalInspectionTest;
import ru.decahthuk.transactionhelperplugin.inspections.TransactionalSelfInvocationInspection;

public class TransactionalSelfInvocationInspectionQuickFixTest extends BaseTransactionalInspectionTest {

    private static final String FIX_TEXT = InspectionBundle.message("inspection.method.transactional.self.invocation.quick-fix.name");

    @Override
    protected String getTestDataPath() {
        return "testData/inspections/transactionalSelfInvocation/quickFix";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new TransactionalSelfInvocationInspection();
    }

    public void testTransactionSelfInvocationHasNoProblems() {
        assertThrows(IllegalStateException.class, "No quick fix found",
                () -> runBasicQuickFixTest("TransactionalSelfInvocationWithNoProblems", FIX_TEXT));
    }

    public void testTransactionSelfInvocationHasProblem_withNoSelf() {
        runBasicQuickFixTest("TransactionalSelfInvocationProblemWithNoSelf", FIX_TEXT);
    }

    public void testTransactionSelfInvocationHasProblem_withExistingSelf() {
        runBasicQuickFixTest("TransactionalSelfInvocationProblemWithExistingSelf", FIX_TEXT);
    }

    public void testTransactionSelfInvocationHasProblem_withMultipleProblems() {
        runBasicQuickFixTest("TransactionalSelfInvocationProblemWithMultipleProblems", FIX_TEXT);
    }
}
