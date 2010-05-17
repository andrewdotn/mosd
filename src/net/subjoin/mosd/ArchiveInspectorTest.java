package net.subjoin.mosd;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ArchiveInspectorTest {
    public @Test void testBasics()
    throws IOException
    {
        TestFile file = Util.loadTestFile("test-archive.tar.gz");
        try {
            DistributionFile[] contents
                = ArchiveInspector.getContents(file.getPath());
            assertEquals(3, contents.length);
            assertEquals("test-archive/foo.txt", contents[0].getPath());
            assertTrue(contents[1].containsOtherFiles());
            assertEquals("inside/baz.txt",
                contents[1].getContainedFiles()[0].getPath());
            assertEquals("test-archive/qux/bar.txt",
                contents[2].getPath());
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
	TestFile file = Util.loadTestFile("test-archive-corrupt.tar");
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
	TestFile file = Util.loadTestFile("test-archive-corrupt.tar.gz");
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
	TestFile file = Util.loadTestFile("not-really-a-tarball.tar.gz");
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

    public @Test void testUsefulErrorMessage() {
	TestFile file = Util.loadTestFile("test-corrupt-inside.tar.gz");
	boolean thrown = false;
	try {
	    ArchiveInspector.getContents(file.getPath());
	} catch (ArchiveInspectorException e) {
	    assertTrue(e.getMessage().contains("innocuous"));
	    assertTrue(e.getMessage().contains("b.zip"));
	    thrown = true;
	} finally {
	    file.close();
	}
	assertTrue(thrown);
    }
    
    public @Test void testCorruptInside()
    throws ArchiveInspectorException
    {
	TestFile file = Util.loadTestFile("test-corrupt-inside.tar.gz");
	final List<String> errors = new ArrayList<String>();
	try {
	    DistributionFile[] files = ArchiveInspector.getContents(file.getPath(),
		    new ArchiveInspectorErrorHandler() {
			public @Override void handleError(String message)
			throws ArchiveInspectorException {
			    errors.add(message);
			}
	    });
	    assertEquals("test-corrupt-inside/b.zip", files[0].getPath());
	    assertTrue(files[0].containsOtherFiles());
	    assertEquals("test-corrupt-inside/foo.txt", files[1].getPath());
	    assertEquals(1, errors.size());
	} finally {
	    file.close();
	}
    }
}
