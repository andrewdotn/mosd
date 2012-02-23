package net.subjoin.mosd.reload;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.subjoin.mosd.DebianControlFile;
import net.subjoin.mosd.DebianControlFileParser;
import net.subjoin.mosd.DistributionFile;
import net.subjoin.mosd.SourcePackage;
import net.subjoin.mosd.UbuntuDistribution;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

public class ReleasePackageAnalyzer {
    private UbuntuDistribution _ub; 
    private Map<String, SourcePackage> _spl;
    
    public ReleasePackageAnalyzer(UbuntuDistribution ub, Map<String, SourcePackage> spl)
    throws Exception
    {
	_ub = ub;
	_spl = spl;
	doit();
    }
    
    public void doit()
    throws Exception
    {
	System.out.println("Sources.gz");
	packageCounts(_ub.getSourcePackageMetadataFiles());
	System.out.println("Packages.gz");
	packageCounts(_ub.getBinaryPackageMetadataFiles());
	
	binaryCountHistogram(_ub.getSourcePackageMetadataFiles());
    }
    
    private void packageCounts(Collection<DistributionFile> dfs)
    throws Exception
    {
	
	int packageCount = 0;
	int cumPackageCount = 0;
	for (DistributionFile df: dfs) {
	    packageCount = 0;

	    try {
		DebianControlFileParser p = new DebianControlFileParser(
			new File(df.getPath()));
		for (DebianControlFile dsc: p) {
		    dsc.getKey("Package");
		   packageCount++;
		   cumPackageCount++;
		}
	    } catch (Exception e) {
		System.err.println(e);
	    }
	    System.out.format("%,6d %,6d %s\n", cumPackageCount, packageCount,
		    Arrays.toString(df.getFullPath()));
	}
    }
    
    private void binaryCountHistogram(Collection<DistributionFile> dfs)
    throws Exception
    {
	Multiset<Integer> hist = HashMultiset.create();
	Set<String> allBinaryPackages = Sets.newHashSet();
	final Map<String, Integer> bigPackages = new HashMap<String, Integer>(); 
	int total = 0;
	for (DistributionFile df: dfs) {
	    DebianControlFileParser p = new DebianControlFileParser(
		    new File(df.getPath()));
	    for (DebianControlFile dsc: p) {
		String[] packages = dsc.getKey("Binary").split(",");
		int binaryCount =  packages.length;
		total += binaryCount;
		hist.add(binaryCount);
		for (String name: packages) {
		    allBinaryPackages.add(name.trim());
		}
		if (binaryCount > 30) {
		    bigPackages.put(dsc.getKey("Package"), binaryCount);
		}
	    }
	}
	System.out.println(total + " total");
	System.out.println(allBinaryPackages.size() + " unique");
	System.out.println(hist);
	
	ArrayList<String> names = new ArrayList<String>(bigPackages.keySet());
	Collections.sort(names, new Comparator<String>() {
	    public @Override int compare(String o1, String o2) {
		return bigPackages.get(o2) - bigPackages.get(o1);
	    }});
	for (String name: names) {
	    System.out.format("%3d %s\n", 
		    bigPackages.get(name), name);
	}
    }
    
}
