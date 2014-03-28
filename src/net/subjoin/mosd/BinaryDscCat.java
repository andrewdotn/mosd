package net.subjoin.mosd;

import java.io.File;
import java.util.Iterator;

public class BinaryDscCat {
    public static void main(String[] args)
    throws Exception
    {
	UbuntuDistribution ub = new UbuntuDistribution("ubuntu", "karmic");
	
	for (DistributionFile df: ub.getBinaryPackageMetadataFiles()) {
	    Iterator<DebianControlFile> it = new DebianControlFileParser(
		    new File(df.getPath())).controlFiles();
	    while (it.hasNext()) {
		DebianControlFile dsc = it.next();
		String packageName = dsc.getKey("Package");
		if (packageName.equals(args[0])) {
			for (String key: dsc.getKeys()) {
			    System.out.println(key + ": " + dsc.getKey(key));
			}
		}
	    }
	}
    }
}

