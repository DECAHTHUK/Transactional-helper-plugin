package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

public class PotentiallyUnwantedNestedTransactionInspectionTest extends BaseTransactionalInspectionTest {

    @Override
    protected String getTestDataPath() {
        return "testData/inspections/nestedUnwanted";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new PotentiallyUnwantedNestedTransactionInspection();
    }

    public void testNestedRequiresNewMethod_withOngoingTransaction() {
        myFixture.configureByFile("NestedRequiresNewWithOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testNestedRequiresNewMethod_withNoOngoingTransaction() {
        myFixture.configureByFile("NestedRequiresNewWithNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testNestedRequiresNewMethod_withNotSupportedUpperTransaction() {
        myFixture.configureByFile("NestedRequiresNewWithNotSupported.java");
        myFixture.checkHighlighting();
    }

    public void testNestedRequiresNewMethod_withSupportsAndNoUpperTransaction() {
        myFixture.configureByFile("NestedRequiresNewWithSupportsAndNoUpper.java");
        myFixture.checkHighlighting();
    }

    public void testNestedRequiresNewMethod_withSupportsAndUpperTransaction() {
        myFixture.configureByFile("NestedRequiresNewWithSupportsAndUpper.java");
        myFixture.checkHighlighting();
    }

    public void testNestedRequiresNewMethod_withNeverAndNoOngoingTransaction() {
        myFixture.configureByFile("NestedRequiresNewWithNeverAndNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testNestedRequiresNewClass_withOngoingTransaction() {
        myFixture.configureByFile("ClassLevelNestedRequiresNewWithOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testNestedRequiresNewClass_withOngoingTransaction_butIncorrectSelfReference() {
        myFixture.configureByFile("NestedRequiresNewWithOngoingButIncorrectSelfRef.java");
        myFixture.checkHighlighting();
    }

    public void testNestedRequiresNewClass_withOngoingTransaction_butCorrectSelfReference() {
        myFixture.configureByFile("NestedRequiresNewWithOngoingButCorrectSelfRef.java");
        myFixture.checkHighlighting();
    }
}

