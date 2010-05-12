package net.subjoin.mosd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class UtilTest {

    public @Test void testLoadResource()
    throws IOException
    {
        assertEquals("Hello, world!\nThis is a test.\n",
            Util.stringResource(UtilTest.class, "test.txt"));
    }

    public @Test void testFileToString()
        throws IOException
    {
        final String testString = "Hello, world!\n";

        Util.runWithTempFileContaining(testString,
            new Util.TempFileUsingRunnable() {
                public void run(File tmpFile) throws IOException {
                    assertEquals(testString, Util.fileToStringMaybeGz(tmpFile));
                }
            });

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            sb.append(testString);
        }
        final String testString2 = sb.toString();
        /* Make sure we exceed the buffer size of the reader method */
        assertTrue(testString2.length() >  0x100000);

        Util.runWithTempFileContaining(testString2,
            new Util.TempFileUsingRunnable() {
                public void run(File tmpFile) throws IOException {
                    assertEquals(testString2, Util.fileToStringMaybeGz(tmpFile));
                }
            });

        /*
         * echo 'Hello, world!' | gzip -c | python3.1 -c "
         *    print([x if x <= 128 else x-256
         *      for x in open('/dev/stdin', 'rb').read()])"
         */
        byte[] gzippedTestBytes = {
            31, -117, 8, 8, -77, -83, -23, 75, 0, 3, 120, 0, -13, 72, -51,
            -55, -55, -41, 81, 40, -49, 47, -54, 73, 81, -28, 2, 0, 24,
            -89, 85, 123, 14, 0, 0, 0
        };

        Util.runWithTempFileContaining(gzippedTestBytes, ".gz",
            new Util.TempFileUsingRunnable() {
                public void run(File tmpFile) throws IOException {
                    assertEquals(testString, Util.fileToStringMaybeGz(tmpFile));
                }
            });
    }
    
    public @Test void testCrazyBufferCopyRoutine()
    throws IOException
    {
	final long length = Util.stringResource(UtilTest.class,
		"test-archive-corrupt.tar").length();
	
	Util.runWithTempFileContainingResource(UtilTest.class,
		"test-archive-corrupt.tar", ".tar",
		new Util.TempFileUsingRunnable() {
		    public void run(File file) throws IOException {
			assertEquals(length, file.length());
		    }
		});
    }
}
