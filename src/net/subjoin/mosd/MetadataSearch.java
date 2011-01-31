package net.subjoin.mosd;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MetadataSearch {
    public static void main(String[] args)
    throws Exception
    {
	UbuntuDistribution ub = new UbuntuDistribution("../ubuntu", "karmic");
	
	Set<String> matchedPackages = Sets.newHashSet();
	

	for (DistributionFile df: ub.getSourcePackageMetadataFiles()) {
	    Iterator<DebianControlFile> it = new DebianControlFileParser(
		    new File(df.getPath())).controlFiles();
	    while (it.hasNext()) {
		DebianControlFile dsc = it.next();
		String packageName = dsc.getKey("Package");
		
		for (String key: dsc.getKeys()) {
		    for (String arg: args) {
			if (key.contains(arg))
			    matchedPackages.add(packageName);
			if (dsc.getKey(key).contains(arg))
			    matchedPackages.add(packageName);
		    }
		}
	    }
	}
	
	List<String> packageNames = Lists.newArrayList(matchedPackages);
	Collections.sort(packageNames);
	for (String s: packageNames)
	    System.out.println(s);
    }
}
