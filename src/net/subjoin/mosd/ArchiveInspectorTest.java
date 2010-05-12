package net.subjoin.mosd;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Test;

public class ArchiveInspectorTest {
    public @Test void testBasics()
    throws IOException
    {
	InputStream is = ArchiveInspectorTest.class
		.getResourceAsStream("test-archive.tar.gz");
	byte[] buffer = new byte[1000000];
	int count = is.read(buffer);
	if (is.read() != -1)
	    throw new RuntimeException("Test file too big for buffer.");
	buffer = Arrays.copyOf(buffer, count);
	
	UtilTest.runWithTempFileContaining(buffer, ".tar.gz",
		new UtilTest.TempFileUsingRunnable() {
		    public void run(File tempFile) throws IOException {
			DistributionFile[] contents
				= ArchiveInspector.getContents(tempFile.getPath());
			System.err.println(Arrays.toString(contents));
			assertEquals(3, contents.length);
			assertEquals("test-archive/foo.txt",
				contents[0].getFile().getPath());
		    }
		});
    
	ArchiveInspector.getContents("foo");
    }
    
    public static void main(String[] args)
    throws IOException
    {
	new ArchiveInspectorTest().testBasics();
    }

}
