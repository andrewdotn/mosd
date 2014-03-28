package net.subjoin.mosd;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BuildEssentialLister {
    
    public static void main(String... args)
    throws Exception
    {
	UbuntuDistribution ub = new UbuntuDistribution("ubuntu", "karmic");
	
	Set<String> matchedPackages = Sets.newHashSet();
	
	for (DistributionFile df: ub.getBinaryPackageMetadataFiles()) {
	    Iterator<DebianControlFile> it = new DebianControlFileParser(
		    new File(df.getPath())).controlFiles();
	    while (it.hasNext()) {
		DebianControlFile dsc = it.next();
		String packageName = dsc.getKey("Package");
		String buildEssential = dsc.getKey("Build-Essential");
		if (buildEssential != null && buildEssential.equals("yes")) {
		    matchedPackages.add(packageName);
		}
		String essential = dsc.getKey("Essential");
		if (essential != null && essential.equals("yes")) {
		    matchedPackages.add(packageName);
		}
	    }
	}
	
	List<String> packageNames = Lists.newArrayList(matchedPackages);
	Collections.sort(packageNames);
	for (String s: packageNames)
	    System.out.println(s);
    }
}

