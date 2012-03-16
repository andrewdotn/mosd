package net.subjoin.mosd.reload;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import net.subjoin.mosd.DistributionFile;
import net.subjoin.mosd.LanguageClassifier;
import net.subjoin.mosd.SourcePackage;
import net.subjoin.mosd.UbuntuDistribution;

public class AsmFinder {
    
    public AsmFinder(UbuntuDistribution ub, Map<String, SourcePackage> spl) {
	SourcePackage sp = spl.get("axiom");
	LanguageClassifier cl = new LanguageClassifier();
	
	Iterator<DistributionFile> it = sp.iterateSourceFiles();
	while (it.hasNext()) {
	    DistributionFile df = it.next();
	    if (cl.classifyFile(df).equals("assembly")) {
		System.out.println(Arrays.toString(df.getFullPath()));
	    }
	}
    }

}
