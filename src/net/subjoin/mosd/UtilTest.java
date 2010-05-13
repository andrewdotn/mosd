package net.subjoin.mosd;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class UtilTest {

    public @Test void testLoadResource()
    throws IOException
    {
        assertEquals("Hello, world!\nThis is a test.\n",
            Util.getTestFileAsString("test.txt"));
    }

    public @Test void testFileToString()
        throws IOException
    {
        final String testString = "Hello, world!\n";
        
        TestFile testFile = Util.loadTestFile("hello.txt");
        try {
            assertEquals(testString, Util.fileToStringMaybeGz(testFile.getFile()));
        } finally {
            testFile.close();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            sb.append(testString);
        }
        final String testString2 = sb.toString();
        /* Make sure we exceed the buffer size of the reader method */
        assertTrue(testString2.length() >  0x100000);
        
        TestFile tmpFile = Util.createTempFileContaining(testString2, "");
        try {
            assertEquals(testString2, Util.fileToStringMaybeGz(tmpFile.getFile()));
        } finally {
            tmpFile.close();
        }
        assertFalse(tmpFile.getFile().exists());

        TestFile file = Util.loadTestFile("hello.txt.gz");
        try {
            assertEquals(testString, Util.fileToStringMaybeGz(file.getFile()));
        } finally {
            file.close();
        }
    }
}
