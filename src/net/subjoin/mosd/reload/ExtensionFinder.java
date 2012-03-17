package net.subjoin.mosd.reload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import net.subjoin.mosd.DistributionFile;
import net.subjoin.mosd.LanguageClassifier;
import net.subjoin.mosd.SourcePackage;
import net.subjoin.mosd.UbuntuDistribution;

public class ExtensionFinder {

    public ExtensionFinder(UbuntuDistribution ub, Map<String, SourcePackage> spl) {
	List<String> sps = new ArrayList<String>(spl.keySet());
	Collections.sort(sps);
	
	Set<String> found = Sets.newHashSet();
	int total = 0;
	
	for (String spName: sps) {
	    SourcePackage sp = spl.get(spName);
	    Iterator<DistributionFile> it = sp.iterateSourceFiles();
	    while (it.hasNext()) {
		DistributionFile df = it.next();
		if (LanguageClassifier.getDfExtension(df).equals(".pamphlet")) {
		    found.add(spName);
		    total++; 
		}
	    }
	}
	
	List<String> sortedFound = new ArrayList<String>(found);
	Collections.sort(sortedFound);
	System.out.println(Joiner.on("\n").join(sortedFound));
	System.out.format("%,d total\n", total);
    }
    
}
