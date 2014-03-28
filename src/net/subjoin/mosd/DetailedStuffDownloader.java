package net.subjoin.mosd;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class DetailedStuffDownloader {
    public static void main(String[] args)
    throws Exception
    {
	UbuntuDistribution ub = new UbuntuDistribution("ubuntu", "karmic");
        List<String> allPkgs = new ArrayList<String>();
	
	Map<String, DebianControlFile> db = Maps.newHashMap();
	for (DistributionFile df: ub.getSourcePackageMetadataFiles()) {
	    Iterator<DebianControlFile> it = new DebianControlFileParser(
		    new File(df.getPath())).controlFiles();
	    while (it.hasNext()) {
		DebianControlFile dsc = it.next();
		String pkg = dsc.getKey("Package");
		db.put(pkg, dsc);
                allPkgs.add(pkg);
	    }
	}

        List<String> pkgs;
        if (args.length > 0) {
            pkgs = Arrays.asList(args);
        } else {
            pkgs = allPkgs;
        }

        System.out.println("set -e");

	for (String pkg: pkgs) {
	    DebianControlFile dsc = db.get(pkg);
	    for (DistributionFile df: dsc.getFiles()) {
		if (df.getPath().endsWith(".dsc")
                        || df.getPath().endsWith(".diff.gz")) {
                    continue;
                }
		System.out.println(
                    "wget --no-host-directories --force-directories"
                    + " --cut-dirs=1"
                    // Most mirrors no longer have all the .orig.tar.gz
                    // files from karmic. In early 2014, this one did.
                    + " http://ftp.cn.debian.org/ubuntu-old-releases/ubuntu/"
		    + df.getPath());
	    }
	}
    }
}
