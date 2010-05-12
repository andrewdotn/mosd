package net.subjoin.mosd;

import java.util.Collections;
import java.util.List;

public class SourcePackage {

    private String _name;
    private List<DistributionFile> _files;
    
    SourcePackage(DebianControlFile dsc) {
	_name = dsc.getKey("Package");
	_files = dsc.getFiles();
    }
    
    public List<DistributionFile> getFiles() {
	return Collections.unmodifiableList(_files);
    }
    
    public String getName() {
	return _name;
    }
    
}
