package net.subjoin.mosd.reload;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.subjoin.mosd.LanguageClassifier;
import net.subjoin.mosd.SourcePackage;
import net.subjoin.mosd.UbuntuDistribution;
import net.subjoin.mosd.Util;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

public class WeightedSampleProperties {
    
    public WeightedSampleProperties(UbuntuDistribution ub, Map<String, SourcePackage> spl) {
	Map<String, Integer> languageCount = Maps.newHashMap();
	List<String> sample = new ArrayList<String>();
	
	List<String> packages = new ArrayList<String>(spl.keySet());
	Collections.sort(packages);
	
	List<String> candidates = new ArrayList<String>();
	
	for (String s: packages) {
	    SourcePackage sp = spl.get(s);
	    LanguageClassifier cl = new LanguageClassifier();
	    cl.classify(sp.iterateSourceFiles());
	    int count = cl.getLanguageCount();
	    if (count < 2)
		continue;
	    
	    languageCount.put(s, count);
	    
	    candidates.add(s);
	    for (int i = 0; i < count; i++) {
		sample.add(s);
	    }
	}
	
	Set<String> unique = new HashSet<String>(sample);
	System.out.format("%,d unique\n", unique.size());
	
	sample = Util.choose(sample, candidates.size(), "multi language systems".hashCode());
	System.out.println(sample.subList(0, 18));
	
	File output = new File("WeightedSampleProperties.out.csv"); 
	try {
	    FileWriter fw = new FileWriter(output);
	    List<Integer> counts = new ArrayList<Integer>();
	    for (String s: candidates) {
		counts.add(languageCount.get(s));
	    }
	    fw.append(Joiner.on(", ").join(counts) + "\n");

	    counts = new ArrayList<Integer>();
	    for (String s: sample) {
		counts.add(languageCount.get(s));
	    }
	    fw.append(Joiner.on(", ").join(counts) + "\n");
	    fw.close();
	    System.out.println("data written to " + output.getAbsolutePath());
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

}
