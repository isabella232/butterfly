package com.paypal.butterfly.test;

import static com.paypal.butterfly.test.Assert.assertAbort;
import static com.paypal.butterfly.test.Assert.assertTransformation;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.File;
import java.util.Iterator;

import com.paypal.butterfly.extensions.springboot.JavaEEToSpringBoot;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AssertTest {
    
    private static final File TEST_RESOURCES = new File("test-resources");

    // Error message header
    private static final String EMH = "Baseline and transformed applications don't match, as detailed below:\n\n";

    @BeforeClass
    public void beforeClass() {

        // This is necessary because Git doesn't allow empty folders to be checked in
        File app3Dir3 = new File(TEST_RESOURCES, "app3/dir3");
        if (!app3Dir3.exists()) {
            app3Dir3.mkdir();
        }
    }

    @Test
    public void sameContentTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app2");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Different file content:\n\t/file1.txt\n")
    public void rootDifferentFileContentTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app8");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Missing in transformed application:\n\t/file1.txt\n")
    public void rootMissingFileTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app10");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Unexpectedly found in transformed application:\n\t/extra_file.txt\n")
    public void rootExtraFileTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app9");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Missing in transformed application:\n\t/dir3 <dir>\n")
    public void rootMissingFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app7");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Unexpectedly found in transformed application:\n\t/dir3 <dir>\n")
    public void rootExtraFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app7");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Different file content:\n\t/dir1/dir2/file2.txt\n")
    public void nonRootDifferentFileContentTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app5");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Missing in transformed application:\n\t/dir3/file3.txt\n")
    public void nonRootMissingFileTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app3");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Unexpectedly found in transformed application:\n\t/dir3/file3.txt\n")
    public void nonRootExtraFileTest() {
        File expected = new File(TEST_RESOURCES, "/app3");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Missing in transformed application:\n\t/dir1/dir2 <dir>\n")
    public void nonRootMissingFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app6");
        assertTransformation(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Unexpectedly found in transformed application:\n\t/dir1/dir2 <dir>\n")
    public void nonRootExtraFolderTest() {
        File expected = new File(TEST_RESOURCES, "/app6");
        File actual = new File(TEST_RESOURCES, "/app1");
        assertTransformation(expected, actual);
    }

    @Test
    public void assertAbortTest() {
        assertAbort(TEST_RESOURCES, JavaEEToSpringBoot.class, true, true, null, "This application does not have a root pom.xml file");
    }

    @Test
    public void semanticXmlComparisonTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app11");
        assertTransformation(expected, actual, true);
    }

    @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = EMH + "Different file content:\n\t/dir1/pom.xml\n")
    public void binaryXmlComparisonTest() {
        File expected = new File(TEST_RESOURCES, "/app1");
        File actual = new File(TEST_RESOURCES, "/app11");
        assertTransformation(expected, actual, false);
    }

    @Test
    public void multipleTest() {
        final String EXPECTED_EXCEPTION_MESSAGE = EMH +
                "Missing in transformed application:\n" +
                    "\t/dir1/dir13 <dir>\n" +
                    "\t/dir1/pom.xml\n" +
                    "\t/dir7 <dir>\n" +
                    "\t/dir8 <dir>\n" +
                    "\t/dir9 <dir>\n" +
                    "\t/file1.txt\n" +
                    "\t/file2.txt\n" +
                "\nUnexpectedly found in transformed application:\n" +
                    "\t/dir1/dir2/dir3 <dir>\n" +
                    "\t/dir1/dir2/dir5 <dir>\n" +
                    "\t/dir3/file5.txt\n" +
                    "\t/dir3/file7.txt\n" +
                    "\t/foo.txt\n" +
                    "\t/pom.xml\n" +
                "\nDifferent file content:\n" +
                    "\t/dir3/file3.txt\n" +
                    "\t/dir3/fileb.txt\n" +
                    "\t/dir3/filec.txt\n";

        File expected = new File(TEST_RESOURCES, "/app12");
        File actual = new File(TEST_RESOURCES, "/app13");

        try {
            assertTransformation(expected, actual, false);
            fail("Expected exception was not thrown");
        } catch (AssertionError e) {
            assertEquals(e.getMessage(), EXPECTED_EXCEPTION_MESSAGE);
        }
    }

}
