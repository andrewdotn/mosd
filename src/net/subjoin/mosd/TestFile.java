package net.subjoin.mosd;

import java.io.File;

/**
 * A unit test data file extracted and written to disk. Users of instances
 * of this class must close() the object when done. This will usually be done
 * in a finally() block.
 */
public class TestFile
{
    private File _file;
    
    public TestFile(File file) {
	_file = file;
    }
    
    public File getFile() {
	return _file;
    }
    
    /* Convenience method that forwards to File. */
    public String getPath() {
	return _file.getPath();
    }
    
    public void close() {
    }
}
