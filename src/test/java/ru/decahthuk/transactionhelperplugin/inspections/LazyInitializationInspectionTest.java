package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInspection.InspectionProfileEntry;
import org.jetbrains.annotations.Nullable;

public class LazyInitializationInspectionTest extends BaseTransactionalInspectionTest {

    @Override
    protected String getTestDataPath() {
        return "testData/inspections/lazyInitialization";
    }

    @Override
    protected @Nullable InspectionProfileEntry getInspection() {
        return new LazyInitializationInspection();
    }

    public void testLazyInitialization_withNoOngoingTransaction() {
        myFixture.configureByFile("LazyInitializationPresentWithNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testLazyInitialization_withOngoingTransaction() {
        myFixture.configureByFile("LazyInitializationPresentWithOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testLazyInitialization_withOngoingClassLevelTransaction() {
        myFixture.configureByFile("LazyInitializationPresentWithClassLevelOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testLazyInitialization_differentRelations_withNoOngoingTransaction() {
        myFixture.configureByFile("LazyInitializationPresentDifferentRelationsWithNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testLazyInitialization_differentEagerRelations_withNoOngoingTransaction() {
        myFixture.configureByFile("LazyInitializationPresentDifferentEagerRelationsWithNoOngoing.java");
        myFixture.checkHighlighting();
    }

    public void testLazyInitialization_withOngoingTransaction_butIncorrectSelfReference() {
        myFixture.configureByFile("LazyInitializationPresentWithOngoingButIncorrectSelfRef.java");
        myFixture.checkHighlighting();
    }

    public void testLazyInitialization_withOngoingTransaction_butCorrectSelfReference() {
        myFixture.configureByFile("LazyInitializationPresentWithOngoingButCorrectSelfRef.java");
        myFixture.checkHighlighting();
    }

    public void testLazyInitialization_withOngoingTransaction_inLambda() {
        myFixture.configureByFile("LazyInitializationPresentWithOngoingInLambda.java");
        myFixture.checkHighlighting();
    }

    public void testLazyInitialization_withNoOngoingTransactionBranch_inLambda() {
        myFixture.configureByFile("LazyInitializationPresentWithNoOngoingBranchInLambda.java");
        myFixture.checkHighlighting();
    }
}
