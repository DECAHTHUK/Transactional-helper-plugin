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
        myFixture.configureByFile("TransactionalSelfInvocationPresentWithoutOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testTransactionalSelfInvocation_withOngoingTransaction() {
        myFixture.configureByFile("TransactionalSelfInvocationPresentWithOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testTransactionalSelfInvocationNotPresent_withNoOngoingTransaction() {
        myFixture.configureByFile("TransactionalSelfInvocationNotPresentWithoutOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testTransactionalSelfInvocation_withClassLevelTransaction() {
        myFixture.configureByFile("TransactionalSelfInvocationPresentWithClassLevel.java");
        myFixture.checkHighlighting();
    }

    public void testTransactionalSelfInvocationMultiple_withNoOngoingTransaction() {
        myFixture.configureByFile("TransactionalSelfInvocationMultiplePresentWithoutOngoing.java");
        myFixture.checkHighlighting();
    }
}
