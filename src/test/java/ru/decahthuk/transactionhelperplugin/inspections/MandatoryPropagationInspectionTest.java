package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

public class MandatoryPropagationInspectionTest extends BaseTransactionalInspectionTest {

    @Override
    protected String getTestDataPath() {
        return "testData/inspections/mandatoryPropagation";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new MandatoryPropagationInspection();
    }

    public void testMandatoryPropagatedMethod_withOngoingTransaction() {
        myFixture.configureByFile("MandatoryPropagationWithOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testMandatoryPropagatedMethod_withNoOngoingTransaction() {
        myFixture.configureByFile("MandatoryPropagationWithNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testMandatoryPropagatedMethod_withABranchWithNoOngoingTransaction() {
        myFixture.configureByFile("MandatoryPropagationWithABranchWithNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testMandatoryPropagatedMethod_withMultipleBranchesWithOngoingTransaction() {
        myFixture.configureByFile("MandatoryPropagationWithMultipleOngoingBranches.java");
        myFixture.checkHighlighting();
    }

    public void testMandatoryPropagatedClass_withABranchWithNoOngoingTransaction() {
        myFixture.configureByFile("ClassLevelMandatoryPropagationWithABranchWithNoOngoing.java");
        myFixture.checkHighlighting();
    }
}
