package net.subjoin.mosd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

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

        runWithTempFileContaining(testString,
            new TempFileUsingRunnable() {
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

        runWithTempFileContaining(testString2,
            new TempFileUsingRunnable() {
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

        runWithTempFileContaining(gzippedTestBytes, ".gz",
            new TempFileUsingRunnable() {
                public void run(File tmpFile) throws IOException {
                    assertEquals(testString, Util.fileToStringMaybeGz(tmpFile));
                }
            });
    }

    private void runWithTempFileContaining(String string,
        TempFileUsingRunnable r)
    throws IOException
    {
        runWithTempFileContaining(string, null, r);
    }

    private void runWithTempFileContaining(String string, String suffix,
        TempFileUsingRunnable r)
    throws IOException
    {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(UtilTest.class.getCanonicalName(),
                suffix);

            FileWriter writer = new FileWriter(tmpFile);
            try {
                writer.append(string);
            } finally {
                writer.close();
            }

            r.run(tmpFile);

        } finally {
            if (tmpFile != null)
                tmpFile.delete();
        }		
    }

    public static void runWithTempFileContaining(byte[] bytes, String suffix,
        TempFileUsingRunnable r)
    throws IOException
    {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(UtilTest.class.getCanonicalName(),
                suffix);

            OutputStream out = new FileOutputStream(tmpFile);
            try {
                out.write(bytes);
            } finally {
                out.close();
            }

            r.run(tmpFile);

        } finally {
            if (tmpFile != null)
                tmpFile.delete();
        }
    }

    public static interface TempFileUsingRunnable {
        void run(File file) throws IOException;
    }
}
