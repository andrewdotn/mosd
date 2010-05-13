package net.subjoin.mosd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UbuntuDistribution {
	private String _releaseName;
	private File _path;
	private List<DistributionFile> _sourcePackageMetadataFiles;
	private List<DistributionFile> _binaryPackageMetadataFiles;
	private List<SourcePackage> _sourcePackages;
	
	public UbuntuDistribution(String path, String releaseName)
	throws IOException
	{
		_path = new File(path);
		_releaseName = releaseName;
		_sourcePackageMetadataFiles
			= new ArrayList<DistributionFile>();
		_binaryPackageMetadataFiles
			= new ArrayList<DistributionFile>();
		
		parseReleaseFile();
	}

	public File getPath() {
		return _path;
	}
	
	private void parseReleaseFile()
	throws IOException
	{
		File releaseFile = new File(getPath(),
			"dists/" + _releaseName + "/Release");
		
		DebianControlFileParser p = new DebianControlFileParser(releaseFile);
		DebianControlFile release = p.controlFile();
		
		for (DistributionFile f: release.getFiles()) {
		    f.setBase(new File(getPath(), "dists/" + _releaseName));
		    if (!f.getDirectory().exists())
			continue;
		    if (f.getName().equals("Sources.gz"))
			_sourcePackageMetadataFiles.add(f);
		    if (f.getName().equals("Packages.gz"))
			_binaryPackageMetadataFiles.add(f);
		}
	}

	public Collection<DistributionFile> getSourcePackageMetadataFiles()
	{
	    return _sourcePackageMetadataFiles;
	}
	
	public Collection<DistributionFile> getBinaryPackageMetadataFiles() {
	    return _binaryPackageMetadataFiles;
	}
	
	public List<SourcePackage> getSourcePackages()
	throws IOException
	{
	    if (_sourcePackages != null)
		return Collections.unmodifiableList(_sourcePackages);
	    
	    List<SourcePackage> sourcePackages = new ArrayList<SourcePackage>();
	    for (DistributionFile f: getSourcePackageMetadataFiles()) {
		DebianControlFileParser p = new DebianControlFileParser(f.getFile());
		for (DebianControlFile dsc: p) {
		    SourcePackage sp = new SourcePackage(dsc);
		    for (DistributionFile spf: sp.getFiles())
			spf.setBase(getPath());
		    sourcePackages.add(sp);
		}
	    }
	    _sourcePackages = sourcePackages;
	    return getSourcePackages();
	}

}
