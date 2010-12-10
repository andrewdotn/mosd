package net.subjoin.mosd;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

public class DetailedStuffDownloader {
    public static void main(String[] args)
    throws Exception
    {
	UbuntuDistribution ub = new UbuntuDistribution("../ubuntu", "karmic");
	
	Map<String, DebianControlFile> db = Maps.newHashMap();
	for (DistributionFile df: ub.getSourcePackageMetadataFiles()) {
	    Iterator<DebianControlFile> it = new DebianControlFileParser(
		    new File(df.getPath())).controlFiles();
	    while (it.hasNext()) {
		DebianControlFile dsc = it.next();
		String pkg = dsc.getKey("Package");
		db.put(pkg, dsc);
	    }
	}
	
	for (String pkg: args) {
	    System.out.println("cd " + pkg);
	    DebianControlFile dsc = db.get(pkg);
	    String[] binaries = dsc.getKey("Binary").split(", ");
	    for (String binary: binaries) {
		System.out.println("apt-cache show " + binary
			+ " > " + binary + ".package");
	    }
	    
	    for (DistributionFile df: dsc.getFiles()) {
		System.out.println("wget http://ca.archive.ubuntu.com/ubuntu/"
		    + df.getPath());

		if (!df.getPath().endsWith(".dsc") && !df.getPath().endsWith(".diff.gz")) {
		    df.scanIfArchive();
		    for (DistributionFile df2: df.getContainedFiles()) {
			if (df2.containsOtherFiles())
			    System.out.println("# " + df2.getPath());
		    }
		}
	    }
	    
	    System.out.println("cd ..");
	}
    }
}
