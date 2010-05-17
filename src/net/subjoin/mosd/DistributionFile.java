package net.subjoin.mosd;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;

/**
 * A file distributed over the internet by an open-source operating system.
 * Anything that you would find on a mirror site. 
 */
public class DistributionFile {
    private String _path;
    private long _size;
    private DistributionFile[] _containedFiles;
    private DistributionFile _enclosingFile;

    public DistributionFile(String path, long size)
    {
	_path = path;
	_size = size;
	_containedFiles = null;
    }
    
    public DistributionFile(String path, long size, DistributionFile[] containedFiles)
    {
	_path = path;
	_size = size;
	setContents(containedFiles);
    }
    
    private void setContents(DistributionFile[] files) {
	for (DistributionFile f: files) {
	    if (f._enclosingFile!= null)
		throw new RuntimeException("Tried to change file container.");
	}
	
	_containedFiles = files;
	for (DistributionFile f: _containedFiles) {
	    f._enclosingFile = this;
	}
    }
    
    public void prependToPath(String path) {
	_path = new File(path, _path).getPath();
    }
    
    public String getPath() {
	return _path;
    }
    
    public String[] getFullPath() {
	List<String> r = Lists.newArrayList();
	DistributionFile file = this;
	while (file != null) {
	    r.add(file.getPath());
	    file = file._enclosingFile;
	}
	Collections.reverse(r);
	return r.toArray(new String[r.size()]); 
    }
    

    public Iterator<DistributionFile> iterateLeaves() {
	return new AbstractIterator<DistributionFile>() {
	    private int _index = -1;
	    private Iterator<DistributionFile> _subIterator = null;
	    
	    public DistributionFile computeNext() {
		if (_subIterator != null) {
		    if (_subIterator.hasNext())
			return _subIterator.next();
		    else
			_subIterator= null;
		}
		if (_index < 0) {
		    _index++;
		    if (!containsOtherFiles())
		      return DistributionFile.this;
		}
		if (_containedFiles == null
			|| _index >= _containedFiles.length)
		    return endOfData();
		DistributionFile curFile =_containedFiles[_index];
		_index++;
		if (curFile.containsOtherFiles()) {
		    _subIterator = curFile.iterateLeaves();
		    return computeNext();
		} else {
		    return curFile;
		}
	    }};
    }	
    
    public long getSize() {
	return _size;
    }
    
    public boolean containsOtherFiles() {
	return _containedFiles != null &&  _containedFiles.length > 0;
    }
    
    public DistributionFile[] getContainedFiles() {
	return _containedFiles;
    }
    
    public DistributionFile getEnclosingFile() {
	return _enclosingFile;
    }
    
    public boolean hasEnclosingFile() {
	return _enclosingFile != null;
    }
    
    public @Override String toString() {
	return _path + " (" + getSize() + ")"
		+ (containsOtherFiles()
			? " " + Arrays.toString(getContainedFiles()) : "");
    }
    
    public boolean hasBeenScanned() {
	return _containedFiles != null;
    }

    public void scanIfArchive()
    throws IOException
    {
	if (_containedFiles != null)
	    return;
	
	DistributionFile[] contents = ArchiveInspector.getContents(getPath(),
		new ArchiveInspectorErrorHandler() {
		    public @Override void handleError(String message)
		    throws ArchiveInspectorException {
			System.err.println(message);
		    }
		});
	setContents(contents);
    }
    
    public @Override boolean equals(Object o) {
	if (!(o instanceof DistributionFile))
	    return false;
	DistributionFile df = (DistributionFile)o;
	if (!_path.equals(df._path)
		|| _size != df._size)
	    return false;
	if (_enclosingFile != df._enclosingFile
		&& (_enclosingFile != null && !_enclosingFile.equals(df._enclosingFile)))
	    return false;
	// FIXME: how to avoid endless recursion?
	if (_containedFiles == df._containedFiles)
	    return true;
	if (_containedFiles == null)
	    return false;
	return _containedFiles.length == df._containedFiles.length;
    }
}