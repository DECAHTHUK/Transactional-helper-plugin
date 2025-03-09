package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.LightProjectDescriptor;
import com.siyeh.ig.LightJavaInspectionTestCase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTransactionalInspectionTest extends LightJavaInspectionTestCase {

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return TransactionalTestUtil.SPRING_DESCRIPTOR;
    }

    protected void runBasicQuickFixTest(String fileNameWithNoExt, String fixText) {
        String fileName = fileNameWithNoExt + ".java";
        myFixture.configureByFile(fileName);
        myFixture.checkHighlighting();

        launchAllMatchingQuickFixes(fileName, fixText);

        myFixture.checkHighlighting(false, false, false, true); // refresh inspection(IDK how to do it properly. Doc is poor)
        myFixture.checkResultByFile(fileNameWithNoExt + "Fixed.java", true);
    }

    protected void launchAllMatchingQuickFixes(@NotNull String fileName, @NotNull String fixText) {
        int cnt = 0;
        List<IntentionAction> fixes = myFixture.getAllQuickFixes(fileName);
        for (IntentionAction fix : fixes) {
            if (fixText.equals(fix.getFamilyName())) {
                myFixture.launchAction(fix);
                cnt++;
            }
        }
        if (cnt == 0) {
            throw new IllegalStateException("No quick fix found");
        }
    }
}
