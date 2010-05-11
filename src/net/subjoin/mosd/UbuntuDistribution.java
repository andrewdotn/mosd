package net.subjoin.mosd;

import java.io.File;
import java.util.Collection;

public class UbuntuDistribution {
	private String _path;
	private Collection<File> _sourcePackageMetadataFiles;
	
	public UbuntuDistribution(String path) {
		_path = path;
	}

	public String getPath() {
		return _path;
	}
	
	private void parseReleaseFile() {
		File releaseFile = new File(getPath(), "Release");
		
	}


	public Collection<File> getSourcePackageMetadataFiles() {
		return _sourcePackageMetadataFiles;
	}

}
