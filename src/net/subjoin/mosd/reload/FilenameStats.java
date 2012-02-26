package net.subjoin.mosd.reload;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Multiset.Entry;
import com.sun.media.jai.rmi.HashSetState;

import net.subjoin.mosd.DistributionFile;
import net.subjoin.mosd.SourcePackage;
import net.subjoin.mosd.UbuntuDistribution;

public class FilenameStats
{
    
    private UbuntuDistribution _ub; 
    private Map<String, SourcePackage> _spl;
    
    public FilenameStats(UbuntuDistribution ub, Map<String, SourcePackage> sp)
    {
	_ub = ub;
	_spl = sp;
	doit();
    }

    public void doit() {
	int count = 0;
	long byteCount = 0;
	Multiset<String> uniqueFileNames = HashMultiset.create();
	boolean printed = false;
	
	for (SourcePackage sp: _spl.values()) {
	    Iterator<DistributionFile> it = sp.iterateSourceFiles();
	    while (it.hasNext()) {
		DistributionFile df = it.next();
		count++;
		byteCount += df.getSize();
		uniqueFileNames.add(new File(df.getPath()).getName());
		
		if (!printed) {
		    printed = true;
		    String[] parts = df.getFullPath();
		    parts[0] = parts[0].substring(10, parts[0].length());
		    System.out.println("first: " + Arrays.toString(parts));
		}
	    }
	}
	
	System.out.format("%,d filenames\n", count);
	System.out.format("%,d bytes\n", byteCount);
	System.out.format("%,d unique file names\n", uniqueFileNames.elementSet().size());
	
	System.out.println("Top file names:\n");
	Set<Integer> counts = new HashSet<Integer>();
	for (Entry<String> e: uniqueFileNames.entrySet()) {
	    counts.add(e.getCount());
	}
	List<Integer> countList = new ArrayList<Integer>();
	List<Entry<String>> top = new ArrayList<Entry<String>>();
	countList.addAll(counts);
	Collections.sort(countList);
	int threshold = countList.get(countList.size() - 50);
	for (Entry<String> e: uniqueFileNames.entrySet()) {
	    if (e.getCount() >= threshold) {
		top.add(e);
	    }
	}
	
	Collections.sort(top, new Comparator<Entry<String>>() {
	    public @Override int compare(Entry<String> o1, Entry<String> o2) {
		return o1.getCount() - o2.getCount();
	    }
	});
	
	for (Entry<String> e: top) {
	    System.out.format("%,10d %s\n", e.getCount(), e.getElement());
	}
    }
}
