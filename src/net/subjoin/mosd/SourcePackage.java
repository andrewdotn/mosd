package net.subjoin.mosd;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class SourcePackage {

    private String _name;
    private List<DistributionFile> _files;
    private boolean _walked;
    private int _uncompressedFileCount;
    private long _uncompressedBytes;
    
    SourcePackage(DebianControlFile dsc)
    {
	_name = dsc.getKey("Package");
	_files = dsc.getFiles();
	_walked = false;
    }
    
    public List<DistributionFile> getFiles() {
	return Collections.unmodifiableList(_files);
    }
    
    public String getName() {
	return _name;
    }
    
    public int getUncompressedFileCount() {
	if (!_walked)
	    walk();
	return _uncompressedFileCount;
    }
    
    private void walk() {
	final int[] count = new int[1];
	final long[] size = new long[1];
	for (DistributionFile df: getFiles()) {
	    File f = new File(df.getPath());
	    if (!f.exists()) {
		if (f.getName().endsWith(".dsc")
			|| f.getName().endsWith(".diff.gz"))
		    continue;
		throw new RuntimeException("missing file " + f);
	    }
	    try {
		walk(ArchiveInspector.getContents(df.getPath()), count, size);
	    } catch (ArchiveInspectorException e) {
		throw new RuntimeException("trying to open " + f, e);
	    }
	}
	_uncompressedFileCount = count[0];
	_uncompressedBytes = size[0];
	_walked = true;
    }
    
    static void walk(DistributionFile[] dfs, int[] count, long[] size)
    {
	for (DistributionFile f: dfs) {
	    if (f.containsOtherFiles())
		walk(f.getContainedFiles(), count, size);
	    else {
		count[0]++;
		size[0] += f.getSize();
	    }
	}
    }
    
    public long getUncompressedBytes() {
	if (!_walked)
	    walk();
	return _uncompressedBytes;
    }
    
}
