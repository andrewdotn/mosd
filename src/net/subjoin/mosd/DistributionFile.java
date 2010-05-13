package net.subjoin.mosd;

import java.io.File;
import java.util.Arrays;

/**
 * A file distributed over the internet by an open-source operating system.
 * Anything that you would find on a mirror site. 
 */
public class DistributionFile {
    private String _path;
    private long _size;
    private DistributionFile[] _containedFiles;
    private DistributionFile _enclosingFile;

    private static final DistributionFile[] EMPTY_ARRAY
    	 = new DistributionFile[0];

    public DistributionFile(String path, long size, DistributionFile[] containedFiles)
    {
	_path = path;
	_size = size;
	_containedFiles = containedFiles;
	for (DistributionFile f: _containedFiles) {
	    if (f._enclosingFile!= null)
		throw new RuntimeException("Tried to change file container.");
	    f._enclosingFile = this;
	}
    }
    
    public DistributionFile(String path, long size)
    {
	this(path, size, EMPTY_ARRAY);
    }
    
    public void prependToPath(String path) {
	_path = new File(path, _path).getPath();
    }
    
    public String getPath() {
	return _path;
    }
    
    public long getSize() {
	return _size;
    }
    
    public boolean containsOtherFiles() {
	return  _containedFiles.length > 0;
    }
    
    public DistributionFile[] getContainedFiles() {
	return Arrays.copyOf(_containedFiles, _containedFiles.length);
    }
    
    public DistributionFile getEnclosingFile() {
	return _enclosingFile;
    }
    
    public boolean hasEnclosingFile() {
	return _enclosingFile != null;
    }
    
    public @Override String toString() {
	return _path + " (" + getSize() + ")"
		+ (containsOtherFiles() ? " " + getContainedFiles() : "");
    }
}