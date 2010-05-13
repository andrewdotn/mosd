package net.subjoin.mosd;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class ArchiveInspectorTest {
    public @Test void testBasics()
    throws IOException
    {
	Util.runWithTempFileContainingResource(
		ArchiveInspectorTest.class, "test-archive.tar.gz", ".tar.gz",
		new Util.TempFileUsingRunnable() {
		    public void run(File tempFile) throws IOException {
			DistributionFile[] contents
				= ArchiveInspector.getContents(tempFile.getPath());
			assertEquals(3, contents.length);
			assertEquals("test-archive/foo.txt",
				contents[0].getFile().getPath());
			assertTrue(contents[1].containsOtherFiles());
			assertEquals("inside/baz.txt",
				contents[1].getContainedFiles()
				.get(0).getFile().getPath());
			assertEquals("test-archive/qux/bar.txt",
				contents[2].getFile().getPath());
		    }
		});
    
    }
    
    public @Test(expected=FileNotFoundException.class)
    void testNonExistentFile()
    throws IOException
    {
	File f = null;
	try {
	    f = File.createTempFile("nonexistent", null);
	    f.delete();
	    ArchiveInspector.getContents(f.getPath());
	} finally {
	    if (f != null)
		f.delete();
	}
    }
    
    public @Test(expected=ArchiveInspectorException.class)
    void testCorruptArchive()
    throws IOException
    {
	Util.runWithTempFileContainingResource(ArchiveInspectorTest.class,
		"test-archive-corrupt.tar", ".tar.gz",
		new Util.TempFileUsingRunnable() {
		    public void run(File tempFile) throws IOException {
			ArchiveInspector.getContents(tempFile.getPath());
		    }
		});
    }

    public @Test(expected=ArchiveInspectorException.class)
    void testCorruptArchive2()
    throws IOException
    {
	Util.runWithTempFileContainingResource(ArchiveInspectorTest.class,
		"test-archive-corrupt.tar.gz", ".tar.gz",
		new Util.TempFileUsingRunnable() {
		    public void run(File tempFile) throws IOException {
			ArchiveInspector.getContents(tempFile.getPath());
		    }
		});
    }
    
    public @Test(expected=ArchiveInspectorException.class)
    void testNotAnArchive()
    throws IOException
    {
	Util.runWithTempFileContainingResource(ArchiveInspectorTest.class,
		"test.txt", ".tar.gz",
		new Util.TempFileUsingRunnable() {
		    public void run(File tempFile) throws IOException {
			ArchiveInspector.getContents(tempFile.getPath());
		    }
		});
    }
    
    public static void main(String[] args)
    throws IOException
    {
	new ArchiveInspectorTest().testBasics();
	for (String s: args)
	    System.out.println(Arrays.toString(ArchiveInspector.getContents(s)));
    }

}
