package net.subjoin.mosd;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class ArchiveInspectorTest {
    public @Test void testBasics()
    throws IOException
    {
        TestFile file = Util.testFile("test-archive.tar.gz");
        try {
            DistributionFile[] contents
                = ArchiveInspector.getContents(file.getPath());
            assertEquals(3, contents.length);
            assertEquals("test-archive/foo.txt", contents[0].getFile().getPath());
            assertTrue(contents[1].containsOtherFiles());
            assertEquals("inside/baz.txt",
                contents[1].getContainedFiles().get(0).getFile().getPath());
            assertEquals("test-archive/qux/bar.txt",
                contents[2].getFile().getPath());
        } finally {
            file.close();
        }
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
	TestFile file = Util.testFile("test-archive-corrupt.tar");
	try {
	    ArchiveInspector.getContents(file.getPath());
	} finally {
	    file.close();
	}
    }

    public @Test(expected=ArchiveInspectorException.class)
    void testCorruptArchive2()
    throws IOException
    {
	TestFile file = Util.testFile("test-archive-corrupt.tar.gz");
	try {
	    ArchiveInspector.getContents(file.getPath());
	} finally {
	    file.close();
	}
    }
    
    public @Test(expected=ArchiveInspectorException.class)
    void testNotAnArchive()
    throws IOException
    {
	TestFile file = Util.testFile("not-really-a-tarball.tar.gz");
	try {
	    ArchiveInspector.getContents(file.getPath());
	} finally {
	    file.close();
	}
    }
    
    private static native boolean shouldLookInside(String pathname);
    
    public @Test void testLookInside() {
	assertFalse(shouldLookInside("foo.txt"));
	assertFalse(shouldLookInside("foo.fartfartfart"));
	assertFalse(shouldLookInside("noextension"));
	assertTrue(shouldLookInside("test.lzma"));
	assertFalse(shouldLookInside("lzma.txt"));
	assertTrue(shouldLookInside("foo.tgz"));
	assertTrue(shouldLookInside("foo.tar"));
	assertTrue(shouldLookInside("foo.zip"));
	assertTrue(shouldLookInside("foo.bar.zip"));

	assertTrue(shouldLookInside("foo.tar.gz"));
	assertFalse(shouldLookInside("foo.txt.gz"));
	assertTrue(shouldLookInside("foo.txt.tar.gz"));	
	assertTrue(shouldLookInside("foo.tar.bz2"));
    }


}
