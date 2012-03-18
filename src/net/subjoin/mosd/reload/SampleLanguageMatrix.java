package net.subjoin.mosd.reload;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.subjoin.mosd.LanguageClassifier;
import net.subjoin.mosd.SourcePackage;
import net.subjoin.mosd.UbuntuDistribution;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SampleLanguageMatrix {
    
    public SampleLanguageMatrix(UbuntuDistribution ub, Map<String, SourcePackage> spl)
    {
	FilenameAnalyzer fa = new FilenameAnalyzer(ub, spl);
	List<SourcePackage> sampleSystems = fa.buildSample(false, 103);
	
	Map<String, Map<String, Integer>> counts = Maps.newHashMap();
	Set<String> langSet = Sets.newHashSet(); 
	
	for (SourcePackage sp: sampleSystems) {
	    LanguageClassifier cl = new LanguageClassifier();
	    cl.classify(sp.iterateSourceFiles());
	    Map<String, Integer> m = cl.getLanguagesWithFileCounts();
	    langSet.addAll(m.keySet());
	    counts.put(sp.getName(), m);
	}
	List<String> sampleNames = new ArrayList<String>();
	for (SourcePackage sp: sampleSystems)
	    sampleNames.add(sp.getName());
	
	List<String> langs = new ArrayList<String>(langSet);
	Collections.sort(langs, new Comparator<String>() {
	    private String transform(String s) {
		return s.toLowerCase().replace(".", "");
	    }
	    
	    public @Override int compare(String s1, String s2) {
		return transform(s1).compareTo(transform(s2));
	    }
	});
	List<String> printLangs = new ArrayList<String>(langs);
	for (int i = 0; i< printLangs.size(); i++) {
	    printLangs.set(i, "\"" + printLangs.get(i) + "\"");
	}
	
	{
	    StringBuilder retSb = new StringBuilder();
	    retSb.append("{\n");
	    List<String> parts = new ArrayList<String>();
	    parts.add("{\"Package\", " + Joiner.on(", ").join(printLangs) + "}");
	    for (SourcePackage sp: sampleSystems) {
		StringBuilder sb = new StringBuilder("{\"" + sp.getName() + "\"");
		for (String l: langs) {
		    Integer i = counts.get(sp.getName()).get(l);
		    if (i == null)
			i = 0;
		    sb.append(", " + i); 
		}
		sb.append("}");
		parts.add(sb.toString());
	    }

	    retSb.append(Joiner.on(",\n").join(parts));
	    retSb.append("}\n");
	    String ret = retSb.toString();
	    System.out.println(ret);
	    try {
		File out = new File("SampleLanguageMatrix.m");
		FileWriter writer = new FileWriter(out);
		writer.write(ret);
		writer.close();
		System.out.println("Written to " + out.getAbsolutePath());
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	}
	
	System.out.println();
	
	{
	    System.out.println();
	    System.out.print("\\begin{tabular}{r|");
	    for (int i = 0; i < sampleSystems.size(); i++)
		System.out.print("r");
	    System.out.println("}");
	    System.out.println("&");
	    ArrayList<String> tmp = new ArrayList<String>();
	    for (SourcePackage sp: sampleSystems) {
		tmp.add("\\rot{" + sp.getName() + "}");
	    }
	    System.out.println(Joiner.on(" &\n").join(tmp));
	    System.out.println("\\\\ \\hline");
	    
	    for (String l: langs) {
		List<String> parts = new ArrayList<String>();
		parts.add(l);
		for (String s: sampleNames) {
		    Integer i = counts.get(s).get(l);
		    if (i == null)
			i = 0;
		    parts.add(i == 0 ? "" : String.format("%,d", i).replace(",", "\\,"));
		}
		System.out.println(Joiner.on(" & ").join(parts) + " \\\\");
	    }
	    System.out.println("\\end{tabular}");
	}

    }
    
}
