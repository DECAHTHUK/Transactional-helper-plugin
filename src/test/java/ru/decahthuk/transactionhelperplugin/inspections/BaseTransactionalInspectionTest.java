package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.testFramework.LightProjectDescriptor;
import com.siyeh.ig.LightJavaInspectionTestCase;
import org.jetbrains.annotations.NotNull;

public abstract class BaseTransactionalInspectionTest extends LightJavaInspectionTestCase {

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return TransactionalTestUtil.SPRING_DESCRIPTOR;
    }
}
