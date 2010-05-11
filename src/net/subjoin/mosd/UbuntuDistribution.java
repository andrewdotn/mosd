package net.subjoin.mosd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UbuntuDistribution {
	private String _path, _releaseName;
	private List<DistributionFile> _sourcePackageMetadataFiles;
	private List<DistributionFile> _binaryPackageMetadataFiles;
	
	public UbuntuDistribution(String path, String releaseName) {
		_path = path;
		_sourcePackageMetadataFiles
			= new ArrayList<DistributionFile>();
		_binaryPackageMetadataFiles
			= new ArrayList<DistributionFile>();
	}

	public String getPath() {
		return _path;
	}
	
	private void parseReleaseFile()
	throws IOException
	{
		File releaseFile = new File(getPath(),
			"dists/" + _releaseName + "/Release");
		
		DebianControlFileParser p = new DebianControlFileParser(releaseFile);
		DebianControlFile release = p.controlFile();
		System.out.println(release);
		
		for (DistributionFile f: release.getFiles()) {
		    if (!f.getDirectory().exists())
			continue;
		    if (f.getName() == "Sources.gz")
			_sourcePackageMetadataFiles.add(f);
		    if (f.getName() == "Packages.gz")
			_binaryPackageMetadataFiles.add(f);
		}
	}

	public Collection<DistributionFile> getSourcePackageMetadataFiles()
	{
	    return _sourcePackageMetadataFiles;
	}
	
	public void load()
	throws IOException
	{
	    parseReleaseFile();
	}

}
