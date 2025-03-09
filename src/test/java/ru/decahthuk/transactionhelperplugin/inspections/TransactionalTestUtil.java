package ru.decahthuk.transactionhelperplugin.inspections;

import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.LanguageLevelModuleExtension;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.IdeaTestUtil;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.MavenDependencyUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public final class TransactionalTestUtil {

    private static final String SPRING_DATA_MODULE = "org.springframework:spring-tx:5.3.0";
    private static final String JAVAX_PERSISTENCE_MODULE = "javax.persistence:javax.persistence-api:2.2";

    private TransactionalTestUtil() {
    }

    public static final DefaultLightProjectDescriptor SPRING_DESCRIPTOR = new DefaultLightProjectDescriptor() {

        @Override
        public void configureModule(@NotNull com.intellij.openapi.module.Module module, @NotNull ModifiableRootModel model, @NotNull ContentEntry contentEntry) {
            super.configureModule(module, model, contentEntry);
            //MavenDependencyUtil.addFromMaven(model, SPRING_DATA_MODULE); // TODO: With migration from java 17 to 11 it stopped working. R.I.P Added manual lib config
            //MavenDependencyUtil.addFromMaven(model, JAVAX_PERSISTENCE_MODULE);
            model.getModuleExtension(LanguageLevelModuleExtension.class).setLanguageLevel(LanguageLevel.JDK_1_8);

            final VirtualFile javaBase = getJarFile("java-base.jar");
            final VirtualFile springData = getJarFile("spring-tx-5.3.0.jar");
            final VirtualFile springBeans = getJarFile("spring-beans-5.3.20.jar");
            final VirtualFile javaxPersistence = getJarFile("javax.persistence-api-2.2.jar");
            PsiTestUtil.newLibrary("javaBase").classesRoot(javaBase).addTo(model);
            PsiTestUtil.newLibrary("springData").classesRoot(springData).addTo(model);
            PsiTestUtil.newLibrary("springBeans").classesRoot(springBeans).addTo(model);
            PsiTestUtil.newLibrary("javaxPersistence").classesRoot(javaxPersistence).addTo(model);
        }

        @Override
        public Sdk getSdk() {
            return IdeaTestUtil.getMockJdk18();
        }
    };

    public static final DefaultLightProjectDescriptor NO_SPRING_DESCRIPTOR = new DefaultLightProjectDescriptor() {

        @Override
        public void configureModule(@NotNull com.intellij.openapi.module.Module module, @NotNull ModifiableRootModel model, @NotNull ContentEntry contentEntry) {
            super.configureModule(module, model, contentEntry);
            model.getModuleExtension(LanguageLevelModuleExtension.class).setLanguageLevel(LanguageLevel.JDK_1_8);
        }

        @Override
        public Sdk getSdk() {
            return IdeaTestUtil.getMockJdk18();
        }
    };


    public static VirtualFile getJarFile(String name) {
        VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(getDataFile(name));
        assertNotNull(file);
        VirtualFile jarFile = JarFileSystem.getInstance().getJarRootForLocalFile(file);
        assertNotNull(jarFile);
        return jarFile;
    }

    private static File getDataFile(String name) {
        return new File("testData/libs/" + name);
    }
}
