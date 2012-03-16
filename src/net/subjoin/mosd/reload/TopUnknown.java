package net.subjoin.mosd.reload;

import java.util.Map;

import net.subjoin.mosd.LanguageClassifier;
import net.subjoin.mosd.SourcePackage;
import net.subjoin.mosd.UbuntuDistribution;

public class TopUnknown
{
    
    private Map<String, SourcePackage> _spl;
    
    public TopUnknown(UbuntuDistribution ub, Map<String, SourcePackage> sp)
    {
	_spl = sp;
	
	LanguageClassifier cl = new LanguageClassifier();
	for (SourcePackage p: _spl.values()) {
	    cl.classify(p.iterateSourceFiles());
	}
	System.out.println(cl.getTopUnknown(25));
    }
}
