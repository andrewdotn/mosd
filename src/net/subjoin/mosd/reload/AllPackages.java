package net.subjoin.mosd.reload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.subjoin.mosd.SourcePackage;
import net.subjoin.mosd.UbuntuDistribution;

public class AllPackages
{
    
    private UbuntuDistribution _ub; 
    private Map<String, SourcePackage> _spl;
    
    public AllPackages(UbuntuDistribution ub, Map<String, SourcePackage> sp)
    {
	_ub = ub;
	_spl = sp;
	
	List<String> packages = new ArrayList<String>();
	for (SourcePackage p: _spl.values()) {
	    packages.add(p.getName());
	}
	Collections.sort(packages);
	for (String s: packages) {
	    System.out.println(s);
	}
    }
}
