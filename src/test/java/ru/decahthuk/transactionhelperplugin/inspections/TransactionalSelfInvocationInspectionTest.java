package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

public class TransactionalSelfInvocationInspectionTest extends BaseTransactionalInspectionTest {

    @Override
    protected String getTestDataPath() {
        return "testData/inspections/transactionalSelfInvocation";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new TransactionalSelfInvocationInspection();
    }

    public void testTransactionalSelfInvocation_withNoOngoingTransaction() {
        myFixture.configureByFile("TransactionalSelfInvocationPresentWithNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testTransactionalSelfInvocation_withOngoingTransaction() {
        myFixture.configureByFile("TransactionalSelfInvocationPresentWithOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testTransactionalSelfInvocationNotPresent_withNoOngoingTransaction() {
        myFixture.configureByFile("TransactionalSelfInvocationNotPresentWithNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testTransactionalSelfInvocation_withClassLevelTransaction() {
        myFixture.configureByFile("TransactionalSelfInvocationPresentWithClassLevel.java");
        myFixture.checkHighlighting();
    }

    public void testTransactionalSelfInvocationMultiple_withNoOngoingTransaction() {
        myFixture.configureByFile("TransactionalSelfInvocationMultiplePresentWithNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testTransactionalSelfInvocationMultiple_withNoOngoingTransaction_butCorrectSelfReference() {
        myFixture.configureByFile("TransactionalSelfInvocationPresentWithNoOngoingButCorrectSelfRef.java");
        myFixture.checkHighlighting();
    }
}
