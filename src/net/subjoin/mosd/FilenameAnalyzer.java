package net.subjoin.mosd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FilenameAnalyzer {
    
    private UbuntuDistribution _ub; 
    private Map<String, SourcePackage> _spl;
    
    public FilenameAnalyzer()
    throws Exception
    {
	_ub = new UbuntuDistribution("../ubuntu", "karmic");
	_spl = Main.loadCache("tasty.cache.gz"); 
    }
    
    public FilenameAnalyzer(UbuntuDistribution ub, Map<String, SourcePackage> sp)
    {
	_ub = ub;
	_spl = sp;
	autoconfStats();
    }
    
    public static void main(String... args)
    throws Exception
    {
	FilenameAnalyzer f = new FilenameAnalyzer();
	f.autoconfStats();
    }
    
    public void autoconfStats() {
	int c_packages = 0;
	int c_packages_with_ac = 0;
	int c_packages_with_am = 0;
	int c_packages_with_ac_and_am = 0;
	int c_packages_with_ac_or_am = 0;
	for (SourcePackage sp: _spl.values()) {
	    LanguageClassifier cl = new LanguageClassifier();
	    cl.classify(sp.iterateSourceFiles());
	    if (cl.getLanguages().contains("C")) {
		c_packages++;
		Iterator<DistributionFile> it = sp.iterateSourceFiles();
		boolean has_ac = false;
		boolean has_am = false;
		while (it.hasNext() && !(has_ac && has_am)) {
		    DistributionFile df = it.next();
		    String ext = cl.getDfExtension(df);
		    if (ext.equals(".ac"))
			has_ac = true;
		    if (ext.equals(".am"))
			has_am = true;
		}
		if (has_ac)
		    c_packages_with_ac++;
		if (has_am)
		    c_packages_with_am++;
		if (has_ac && has_am)
		    c_packages_with_ac_and_am++;
		if (has_ac || has_am)
		    c_packages_with_ac_or_am++;
	    }
	}
	System.out.format("%,d c packages\n%,d am\n%,d ac\n%,d both\n%,d either\n",
		c_packages, c_packages_with_am, c_packages_with_ac,
		c_packages_with_ac_and_am,
		c_packages_with_ac_or_am);
    }
    
    public void printOverallClassification() {
	LanguageClassifier cl = new LanguageClassifier();
	for (SourcePackage sp: _spl.values()) {
	    cl.classify(sp.iterateSourceFiles());
	}
	System.out.println(cl.getLanuageClassification());
    }
    
    public void printSampleStats() {
	List<SourcePackage> sample = buildSample();
	for (SourcePackage sp: sample) {
	    LanguageClassifier cl = new LanguageClassifier();
	    cl.classify(sp.iterateSourceFiles());
	    System.out.println(sp.getName());
	    System.out.println();
	    System.out.println("Language count: " + cl.getLanguageCount());
	    System.out.println();
	    String s = cl.getLanuageClassification();
	    s = s.substring(0, 
		    s.indexOf("Languages by bytes"));
	    System.out.println(s);
	    System.out.println();
	}
    }
    
    public List<SourcePackage> buildSample() {	
	int numml = 0;
	List<SourcePackage> sample = new ArrayList<SourcePackage>();
	List<String> names= new ArrayList<String>();
	for (SourcePackage sp: _spl.values())
	    names.add(sp.getName());
	Collections.sort(names);
	for (String s: names) {
	    SourcePackage sp = _spl.get(s);
	    LanguageClassifier cl = new LanguageClassifier();
	    cl.classify(sp.iterateSourceFiles());
	    int languageCount = cl.getLanguageCount();
	    if (languageCount < 2)
		continue;
	    numml++;
	    for (int i = 0; i < languageCount; i++)
		sample.add(sp);
	}
	System.out.println(_spl.size() + " packages, "
		+ numml + " multi-language packages, "
		+ sample.size() + " entries in sample.");

	sample = Util.choose(sample, 18, "multi language systems".hashCode());
	System.out.println("Sample:");
	
	List<String> ret = new ArrayList<String>();
	for (SourcePackage sp: sample)
	    ret.add(sp.getName());
	
	System.out.println(Arrays.toString(ret.toArray()));

	return sample;
    }
    

}
