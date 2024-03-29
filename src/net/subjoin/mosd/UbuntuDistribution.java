package net.subjoin.mosd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;

public class UbuntuDistribution {
	private String _releaseName;
	private String _path;
	private List<DistributionFile> _sourcePackageMetadataFiles;
	private List<DistributionFile> _binaryPackageMetadataFiles;
	private List<SourcePackage> _sourcePackages;
	
	public UbuntuDistribution(String path, String releaseName)
	throws IOException
	{
		_path = path;
		_releaseName = releaseName;
		_sourcePackageMetadataFiles
			= new ArrayList<DistributionFile>();
		_binaryPackageMetadataFiles
			= new ArrayList<DistributionFile>();
		
		parseReleaseFile();
	}

	public String getPath() {
		return _path;
	}
	
	public String getReleaseName() {
	    return _releaseName;
	}
	
	private void parseReleaseFile()
	throws IOException
	{
		File releaseFile = new File(getPath(),
			"dists/" + _releaseName + "/Release");
		
		DebianControlFileParser p = new DebianControlFileParser(releaseFile);
		DebianControlFile release = p.controlFile();
		
		for (DistributionFile f: release.getFiles()) {
		    f.prependToPath(new File(getPath(), "dists/" + _releaseName).getPath());
		    File rf = new File(f.getPath()); 
		    if (!rf.getParentFile().exists())
			continue;
		    if (rf.getName().equals("Sources.gz"))
			_sourcePackageMetadataFiles.add(f);
		    if (rf.getName().equals("Packages.gz"))
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
		DebianControlFileParser p = new DebianControlFileParser(
			new File(f.getPath()));
		for (DebianControlFile dsc: p) {
		    SourcePackage sp = new SourcePackage(dsc);
		    for (DistributionFile spf: sp.getFiles())
			spf.prependToPath(getPath());
		    sourcePackages.add(sp);
		}
	    }
	    _sourcePackages = sourcePackages;
	    return getSourcePackages();
	}
	
	public static Iterator<DistributionFile> iterateAllSourceFiles(
		final Iterable<SourcePackage> spit)
	{
	    return new AbstractIterator<DistributionFile>() {
		Iterator<SourcePackage> metait = spit.iterator();
		Iterator<DistributionFile> it = Iterators.emptyIterator();
		protected @Override DistributionFile computeNext() {
		    if (it.hasNext())
			return it.next();
		    if (!metait.hasNext())
			return endOfData();
		    it = metait.next().iterateSourceFiles();
		    return computeNext();
		}
	    };
	}
}
