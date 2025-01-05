package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

public class NeverPropagationInspectionTest extends BaseTransactionalInspectionTest {

    @Override
    protected String getTestDataPath() {
        return "testData/inspections/neverPropagation";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new NeverPropagationInspection();
    }

    public void testNeverPropagatedMethod_withOngoingTransaction() {
        myFixture.configureByFile("NeverPropagationWithOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testNeverPropagatedMethod_withNoOngoingTransaction() {
        myFixture.configureByFile("NeverPropagationWithNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testNeverPropagatedMethod_withNotSupportedUpperTransaction() {
        myFixture.configureByFile("NeverPropagationWithNotSupported.java");
        myFixture.checkHighlighting();
    }

    public void testNeverPropagatedMethod_withSupportsAndNoUpperTransaction() {
        myFixture.configureByFile("NeverPropagationWithSupportsAndNoUpper.java");
        myFixture.checkHighlighting();
    }

    public void testNeverPropagatedMethod_withSupportsAndUpperTransaction() {
        myFixture.configureByFile("NeverPropagationWithSupportsAndUpper.java");
        myFixture.checkHighlighting();
    }

    public void testNeverPropagatedMethod_withNeverAndNoOngoingTransaction() {
        myFixture.configureByFile("NeverPropagationWithNeverAndNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testNeverPropagatedClass_withOngoingTransaction_butIncorrectSelfReference() {
        myFixture.configureByFile("NeverPropagationWithOngoingButIncorrectSelfRef.java");
        myFixture.checkHighlighting();
    }

    public void testNeverPropagatedClass_withOngoingTransaction_butCorrectSelfReference() {
        myFixture.configureByFile("NeverPropagationWithOngoingButCorrectSelfRef.java");
        myFixture.checkHighlighting();
    }
}
