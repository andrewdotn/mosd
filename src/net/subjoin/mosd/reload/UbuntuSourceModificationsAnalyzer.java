package net.subjoin.mosd.reload;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.subjoin.mosd.DistributionFile;
import net.subjoin.mosd.SourcePackage;
import net.subjoin.mosd.UbuntuDistribution;

public class UbuntuSourceModificationsAnalyzer {

    private static final Pattern RE_UBUNTU_MODIFIED_PACKAGE
    	= Pattern.compile("ubuntu[0-9]*\\.tar\\.gz$");
    
    // private UbuntuDistribution _ub; 
    private Map<String, SourcePackage> _spl;
    
    public UbuntuSourceModificationsAnalyzer(UbuntuDistribution ub, Map<String, SourcePackage> spl)
    throws Exception
    {
	// _ub = ub;
	_spl = spl;
	doit();
    }
    
    public void doit()
    throws Exception
    {
	int count = 0;
	int countNonOrigTarGz = 0;
	List<String> found = new ArrayList<String>();
	for (SourcePackage sp: _spl.values()) {
	    for (DistributionFile df: sp.getFiles()) {
		
		if (df.getPath().endsWith(".dsc") || df.getPath().endsWith(".diff.gz"))
		    continue;
		
		count++;
		
		Matcher m1 = RE_UBUNTU_MODIFIED_PACKAGE.matcher(df.getPath());
		if (m1.find()) {
		    found.add(new File(df.getPath()).getName());
		    countNonOrigTarGz++;
		}
	    }
	}
	Collections.sort(found);
	for (String s: found) {
	    System.out.println(s);
	}
	System.out.format("%,d total non-dsc non-diff.gz files\n", count);
	System.out.format("%,d of those non-orig.tar.gz\n", countNonOrigTarGz);
    }
    
}
