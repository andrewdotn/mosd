package net.subjoin.mosd;

import java.io.File;

/**
 * A file distributed over the internet by an open-source operating system.
 * Anything that you would find on a mirror site. 
 */
public class DistributionFile {
    File _base;
    String _path;
    long _size;
    
    public DistributionFile(String base, String path, long size)
    {
	_base = null;
	_path = new File(base, path).getPath();
	_size = size;
    }
    
    public String getName() {
	return new File(_base, _path).getName();
    }
    
    public File getFile() {
	return new File(_base, _path);
    }
    
    public File getDirectory() {
	return getFile().getParentFile();
    }
    
    public void setBase(File base) {
	_base = base;
    }
    
    public long getSize() {
	return _size;
    }
    
    public @Override String toString() {
	return getFile().getPath() + " [" + getSize() + "]";
    }
}
