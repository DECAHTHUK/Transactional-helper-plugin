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

    public void testNestedRequiresNewMethod_withUpperTransaction() {
        myFixture.configureByFile("NestedRequiresNewTransactional.java");
        myFixture.checkHighlighting();
    }

    public void testNestedRequiresNewMethod_noUpperTransaction() {
        myFixture.configureByFile("NestedRequiresNewWithoutTransaction.java");
        myFixture.checkHighlighting();
    }
}
