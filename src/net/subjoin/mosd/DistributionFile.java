package net.subjoin.mosd;

import java.io.File;

/**
 * A file distributed over the internet by an open-source operating system.
 * Anything that you would find on a mirror site. 
 */
public class DistributionFile {
    String _base;
    String _path;
    long _size;
    
    public DistributionFile(String base, String path, long size)
    {
	_base = base;
	_path = path;
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
    
    public long size() {
	return _size;
    }

}
