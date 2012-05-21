package net.subjoin.mosd.reload;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import net.subjoin.mosd.DistributionFile;
import net.subjoin.mosd.LanguageClassifier;
import net.subjoin.mosd.SourcePackage;
import net.subjoin.mosd.UbuntuDistribution;

public class DFSGFinder {
    
    public DFSGFinder(UbuntuDistribution ub, Map<String, SourcePackage> spl) {
	for (SourcePackage sp: spl.values()) {
	    Iterator<DistributionFile> it = sp.iterateSourceFiles();

	while (it.hasNext()) {
	    DistributionFile df = it.next();
	    if (LanguageClassifier.getDfExtension(df).equals(".dfsg"))
		System.out.println(Arrays.toString(df.getFullPath()));
	    }
	}
    }

}
