package net.subjoin.mosd;

import static com.martiansoftware.jsap.JSAP.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;

public class Main {
    
    private static final String OPT_SLICE = "first n packages";
    private static final String OPT_LIMIT = "limit for summary displays";
    private static final String OPT_FIND = "find file matching regex";
    private static final String OPT_WRITE_CACHE = "write cache file";
    private static final String OPT_INTERACTIVE = "interactive mode";
    private static final String OPT_LANGCOUNT
    	= "show info for packages with this many languages";
    
	public static void main(String[] args)
	throws Exception
	{
	    SimpleJSAP jsap = new SimpleJSAP("mosd", "",
		    new Parameter[] {
		    new FlaggedOption(OPT_SLICE).setShortFlag('n')
		    	.setStringParser(INTEGER_PARSER),
		    new FlaggedOption(OPT_LIMIT).setShortFlag('l')
		    	.setStringParser(INTEGER_PARSER).setDefault("10"),
		    new FlaggedOption(OPT_FIND).setShortFlag('f'),
		    new FlaggedOption(OPT_LANGCOUNT).setShortFlag('L')
		    	.setStringParser(INTEGER_PARSER).setDefault("2"),
		    new Switch(OPT_WRITE_CACHE).setShortFlag('w'),
		    new Switch(OPT_INTERACTIVE).setShortFlag('i')
	    });
	    
	    JSAPResult config = jsap.parse(args);
	    if (jsap.messagePrinted())
		System.exit(1);
	    
		UbuntuDistribution ub = new UbuntuDistribution("../ubuntu", "karmic");
		
//		System.out.println("The distribution at " + ub.getPath());
//		
//		System.out.println("has source package metadata files at:");	
//		for (Object o: ub.getSourcePackageMetadataFiles())
//			System.out.println("\t" + o);
//
//		System.out.println("and binary package metadata files at:");
//		for (Object o: ub.getBinaryPackageMetadataFiles())
//			System.out.println("\t" + o);
//		
		System.out.print("I know about "
				+ ub.getSourcePackages().size()
				+ " source packages ");
		
		{
		long totalBytes = 0;
		for (SourcePackage sp: ub.getSourcePackages()) {
		    for (DistributionFile f: sp.getFiles())
			totalBytes += f.getSize();
		}
		System.out.format("totalling %,d bytes%n", totalBytes);
		}
		

//		{
//		    SourcePackage sp = ub.getSourcePackages().get(0);
//		    List<DistributionFile> files = sp.getFiles();
//		    for (DistributionFile df: files) {
//			System.out.print("It has file " + df.getPath());
//			if (new File(df.getPath()).exists()) {
//			    System.out.println(" which has files " + 
//				    Arrays.toString(ArchiveInspector.getContents(
//					    df.getPath())));
//			} else {
//			    System.out.println(" .. which we don’t have a copy of.");
//			}
//		    }
//		}
		
		File cacheFile = new File("tasty.cache.gz");
		Map<String, SourcePackage> cache = ImmutableMap.of();

		int limit = config.getInt(OPT_SLICE, Integer.MAX_VALUE - 1);
		if (cacheFile.exists()) {
        		ObjectInputStream is = new ObjectInputStream(
				new GZIPInputStream(
        			new BufferedInputStream(
        				new FileInputStream(cacheFile))));
        		cache = Maps.newHashMap();
        		int i = 0;
        		while (i++ < limit) {
        		    try {
                		    SourcePackage sp = (SourcePackage)is.readObject();
                		    cache.put(sp.getName(), sp);
                		    
                		    if (config.contains(OPT_FIND)) {
                			findFiles(sp, config.getString(OPT_FIND));
                		    }
        		    } catch (EOFException e) {
        			// there’s no method to check if end-of-file
        			// has been reached, unfortunately. 
        			break;
        		    }
        		}
        		is.close();
		}
		
		List<SourcePackage> spl = new ArrayList<SourcePackage>();
//			ub.getSourcePackages());
//		
		if (cacheFile.exists()) {
//        		for (int i = 0; i < spl.size(); i++) {
//        		    String name = spl.get(i).getName();
//        		    SourcePackage sp = cache.get(name);
//        		    if (sp != null)
//        			spl.set(i, sp);
//        		}
		    for (SourcePackage sp: cache.values())
			spl.add(sp);
		}
		System.out.format("%d packages in cache\n", spl.size());
		
		boolean countMakeFilePackages = true;
		if (countMakeFilePackages) {
		    int count = 0;
		    for (SourcePackage sp: spl) {
			Iterator<DistributionFile> itdf = sp.iterateSourceFiles();
			while (itdf.hasNext()) {
			    DistributionFile df = itdf.next();
			    String s = LanguageClassifier.getBasename(df.getPath());
			    if (s.equals("Makefile") || s.equals("makefile") || s.equals("GNUMakefile")) {
				count++;
				break;
			    }
			}
		    }
		    System.out.format("%,d (%2.0f%%) contain Makefiles\n",
			    count, 100. * count / spl.size());
		}
		
		{
		    int totalFiles = 0;
		    long totalBytes = 0;

		    Iterator<DistributionFile> itdf
        			= UbuntuDistribution.iterateAllSourceFiles(spl);
        		while (itdf.hasNext()) {
        		    DistributionFile df = itdf.next();
        		    totalFiles++;
        		    totalBytes += df.getSize();
        		}
        		System.out.format("uncompressed total %,d files, %,d bytes%n",
        			totalFiles, totalBytes);
        		
        		totalFiles = 0;
        		totalBytes = 0;
        		
        		for (SourcePackage sp: spl) {
        		    totalFiles += sp.getUncompressedFileCount();
        		    totalBytes += sp.getUncompressedBytes();
        		}

        		System.out.format("uncompressed total %,d files, %,d bytes%n",
        			totalFiles, totalBytes);
		}
		
		System.out.format("%d source packages, %d in cache\n",
			ub.getSourcePackages().size(),
			spl.size());
		
		{
		    Set<String> s = new HashSet<String>();
		    for (SourcePackage sp: ub.getSourcePackages())
			s.add(sp.getName());
		    System.out.println(s.size() + " unique package names\n");
		}
		
		{
		    LanguageClassifier cl = new LanguageClassifier();
		    cl.classify(UbuntuDistribution.iterateAllSourceFiles(spl));
		    System.out.println(cl.getTopUnknown(config.getInt(OPT_LIMIT)));
		}
		
//		for (SourcePackage sp: spl) {
//		    System.out.println(sp.getName());
//		    for (int i = 0; i < sp.getName().length(); i++)
//			System.out.print('=');
//		    System.out.println();
//		    LanguageClassifier cl = new LanguageClassifier();
//		    cl.classify(sp.iterateSourceFiles());
//		    System.out.println(cl.getTopUnknown(config.getInt(OPT_LIMIT)));
//		    System.out.println(cl.getLanuageClassification());
//		}
		
		
		
//		for (SourcePackage sp: spl) {
//		    Iterator<DistributionFile> itdf = sp.iterateSourceFiles();
//		    LanguageClassifier cl = new LanguageClassifier();
//		    cl.classify(itdf);
//		    
//		    int totalFiles = cl.counts.size();
//		    int unknown = cl.counts.count("unknown");
//		    int other = cl.counts.count("other");
//		    int knownFiles = totalFiles - unknown - other;
//		    
//		    int min = (int)Math.max(10, Math.round(0.15 * knownFiles));  
//		    
//		    List<String> ss = new ArrayList<String>();
//		    int numLangs = 0;
//		    for (String s: cl.counts.elementSet()) {
//			if (s.equals("other") || s.equals("unknown")
//				|| s.equals("build") || s.equals("make")
//				|| s.equals("automake"))
//			    continue;
//			if (cl.counts.count(s) > min) {
//			    ss.add(s);
//			    numLangs++;
//			}
//		    }
//		    
//		    if (numLangs < 2)
//			continue;
//		    
//		    System.out.format("%s,%d,%2.0f%%,%d",
//			    sp.getName(), sp.getUncompressedFileCount(),
//			    100 * (double)knownFiles / (totalFiles - other),
//			    numLangs);
//		    for (String s: ss)
//			System.out.format(", %s (%2.0f%%)", s, 100. * cl.counts.count(s) / knownFiles);
//		    System.out.println();
//		}
		
		if (config.getBoolean(OPT_WRITE_CACHE)) {
		    System.out.println("Writing new cache...\n");
		    long totalBytes = 0;
        		long totalFiles = 0;
        		int i = 0;
        		File newCacheFile = new File(cacheFile.getName() + ".new");
        		ObjectOutputStream os = new ObjectOutputStream(
        			new BufferedOutputStream(
        				new GZIPOutputStream(
        				new FileOutputStream(newCacheFile))));
        		for (SourcePackage sp: ub.getSourcePackages()) {
        		    try {
        			if (cache.containsKey(sp.getName()))
        			    sp = cache.get(sp.getName());
        			
        			totalBytes += sp.getUncompressedBytes();
        			totalFiles += sp.getUncompressedFileCount();
        			
        			os.writeObject(sp);
        		    } catch (RuntimeException e) {
        			System.err.println(e);
        		    }
        		    
        //		    System.out.format("%s %d/%d, %s files, %s bytes\n",
        //			    sp.getName(), i + 1, spl.size(),
        //			    nf.format(totalFiles),
        //			    nf.format(totalBytes));
        		    i++;
        		}
        		System.out.format("total %,d files, %,d bytes uncompressed",
        			totalFiles, totalBytes);
        		os.close();
        		if (!newCacheFile.renameTo(cacheFile))
        		    throw new RuntimeException("couldn’t rename file");
		}
		
//		List<Entry<String, Integer>> langcounts = Lists.newArrayList();
//		for (SourcePackage sp: spl) {
//		    LanguageClassifier cl = new LanguageClassifier();
//		    cl.classify(sp.iterateSourceFiles());
//		    langcounts.add(new SimpleImmutableEntry<String, Integer>(
//			    sp.getName(), cl.getLanguageCount()));
//		}
//		Collections.sort(langcounts, new Comparator<Entry<String, Integer>>() {
//		    public @Override int compare(Entry<String, Integer> a,
//			    Entry<String, Integer> b)
//		    {
//			return Long.signum((long)b.getValue() - a.getValue());
//		    }
//		});
//		{
//		    int i = 0;
//		    for (Entry<String, Integer> e: langcounts) {
//			System.out.format("%4d %s\n", e.getValue(), e.getKey());
//			if (i++ > 50)
//			    break;
//		    }
//		}
		
//		// Count and sample packages by language count
//		List<SourcePackage> specificCountPackages = new ArrayList<SourcePackage>();
//		AccumulatingMap<List<String>> oneLangs = AccumulatingMap.create();
//		final int targetLangCount = config.getInt(OPT_LANGCOUNT);
//		System.out.println("Langcount counts");
//		AccumulatingMap<Integer> langCountCounts = AccumulatingMap.create();
//		for (SourcePackage sp: spl) {
//		    LanguageClassifier cl = new LanguageClassifier();
//		    cl.classify(sp.iterateSourceFiles());
//		    int langCount = cl.getLanguageCount();
//		    langCountCounts.increment(langCount);
//		    if (langCount == targetLangCount) {
//			specificCountPackages.add(sp);
//			List<String> l = Lists.newArrayList(cl.getLanguages());
//			Collections.sort(l);
//			oneLangs.increment(l);
//		    }
//		}
//		List<Integer> c = new ArrayList<Integer>(langCountCounts.keys());
//		Collections.sort(c);
//		for (Integer i: c) {
//		    System.out.format("%,6d %d\n", langCountCounts.get(i), i);
//		}
//		System.out.println(oneLangs.getTopEntries(Integer.MAX_VALUE - 1));
//
//		if (specificCountPackages.size() > 15)
//		    specificCountPackages = Util.choose(specificCountPackages, 15,
//			    "n-language package".hashCode());
//		for (SourcePackage sp: specificCountPackages) {
//		    System.out.println("Selected " + sp.getName());
//		    LanguageClassifier cl = new LanguageClassifier();
//		    cl.classify(sp.iterateSourceFiles());
//		    System.out.println(cl.getLanuageClassification());
//		}
		
		{
		int numml = 0;
		List<SourcePackage> sample = new ArrayList<SourcePackage>();
		List<String> packs = new ArrayList<String>();
		for (SourcePackage sp: spl)
		    packs.add(sp.getName());
		Collections.sort(packs);
		for (String s: packs) {
		    SourcePackage sp = cache.get(s);
		    LanguageClassifier cl = new LanguageClassifier();
		    cl.classify(sp.iterateSourceFiles());
		    int languageCount = cl.getLanguageCount();
//		    System.out.format("%3d %s\n", languageCount, sp.getName());
		    if (languageCount < 2)
			continue;
		    numml++;
		    for (int i = 0; i < languageCount; i++)
			sample.add(sp);
		    
		}
		System.out.println(spl.size() + " packages, "
			+ numml + " multi-language packages, "
			+ sample.size() + " entries in sample.");

		sample = Util.choose(sample, 200, "multi language systems".hashCode());
		System.out.println("Sample:");
		System.out.println("#!/bin/bash");
		System.out.println("set -eu");
		int i = 0;
		for (SourcePackage sp: sample) {
		    i++;
		    System.out.println("# " + sp.getName());
//		    String dir = String.format("%04d", i);
//		    System.out.format("rmdir %s\nmkdir %s\ncd %s\n", dir, dir, dir);
//		    for (DistributionFile df: sp.getFiles())
//			if (SourcePackage.isUpstreamFile(df))
//			    System.out.println("tar xf " + new File("..", df.getPath()));
//		    System.out.println("cd ..");
		}
		}
		
		{
		    System.out.println();
		    System.out.println("evaluation");
			int numml = 0;
			List<SourcePackage> sample = new ArrayList<SourcePackage>();
			List<String> packs = new ArrayList<String>();
			for (SourcePackage sp: spl)
			    packs.add(sp.getName());
			Collections.sort(packs);
			for (String s: packs) {
			    SourcePackage sp = cache.get(s);
			    LanguageClassifier cl = new LanguageClassifier();
			    cl.classify(sp.iterateSourceFiles());
			    int languageCount = cl.getLanguageCount();
//			    System.out.format("%3d %s\n", languageCount, sp.getName());
			    if (languageCount < 2)
				continue;
			    numml++;
			    for (int i = 0; i < languageCount; i++)
				sample.add(sp);
			    
			}
			System.out.println(spl.size() + " packages, "
				+ numml + " multi-language packages, "
				+ sample.size() + " entries in sample.");

			sample = Util.choose(sample, 15, "evaluation".hashCode());
			int i = 0;
			for (SourcePackage sp: sample) {
			    i++;
			    System.out.println("# " + sp.getName());
			    String dir = String.format("%04d", i);
			    System.out.format("rmdir %s\nmkdir %s\ncd %s\n", dir, dir, dir);
			    for (DistributionFile df: sp.getFiles())
				if (SourcePackage.isUpstreamFile(df))
				    System.out.println("tar xf " + new File("..", df.getPath()));
			    System.out.println("cd ..");
			}
		}
		
		if (!config.getBoolean(OPT_INTERACTIVE))
		    return;
		
		System.out.print("> ");
		Scanner s = new Scanner(System.in);
		while (s.hasNextLine()) {
		    String[] cmd = s.nextLine().split("\\s+");
		    if (cmd[0].equals("unknown")) {
			if (cmd.length > 1) {
			    for (String packageName:  Arrays.copyOfRange(cmd, 1, cmd.length)) {
				SourcePackage sp = cache.get(packageName);
				if (sp == null) {
				    System.out.println("not found.");
				    break;
				}				
				System.out.println(sp.getName());
				LanguageClassifier cl = new LanguageClassifier();
				cl.classify(sp.iterateSourceFiles());
				System.out.println(cl.getTopUnknown());
			    }
			}
		    } else if (cmd[0].equals("class")) {
			if (cmd.length > 1) {
			    for (String packageName:  Arrays.copyOfRange(cmd, 1, cmd.length)) {
				SourcePackage sp = cache.get(packageName);
				if (sp == null) {
				    System.out.println("not found.");
				    break;
				}
				System.out.println(sp.getName());
				LanguageClassifier cl = new LanguageClassifier();
				cl.classify(sp.iterateSourceFiles());
				System.out.println(cl.getLanuageClassification());
			    }
			}			
		    } else {
			System.out.println("?");
		    }
		    System.out.print("> ");
		}
	}

	private static void findFiles(SourcePackage sp, String string) {
	    Pattern p = Pattern.compile(string);
	    Iterator<DistributionFile> itdf = sp.iterateSourceFiles();
	    while (itdf.hasNext()) {
		DistributionFile df = itdf.next();
		if (p.matcher(df.getPath()).matches()) {
		    String[] fullPath = df.getFullPath();
		    System.out.format("%,12d %s\n", df.getSize(),
			    Joiner.on(" ").join(Arrays.copyOfRange(fullPath, 0, fullPath.length))); 
		}
	    }
	}
}
