package net.subjoin.mosd;

import java.io.IOException;
import java.nio.charset.Charset;

public class Main {
	
	public static void main(String[] args)
	throws IOException
	{
		UbuntuDistribution ub = new UbuntuDistribution(args[0], args[1]);
		ub.load();
		
		System.out.println("The distribution at " + ub.getPath());
		
		System.out.println("has source package metadata files at:");	
		for (Object o: ub.getSourcePackageMetadataFiles())
			System.out.println("\t" + o);

		System.out.println("and binary package metadata files at:");
		for (Object o: ub.getBinaryPackageMetadataFiles())
			System.out.println("\t" + o);
		
		System.out.println("I know about "
				+ ub.getSourcePackages().size()
				+ " source packages");
		
		long totalBytes = 0;
		for (SourcePackage sp: ub.getSourcePackages()) {
		    for (DistributionFile f: sp.getFiles())
			totalBytes += f.getSize();
		}
		System.out.println("totalling " + totalBytes + " bytes.");
		
		SourcePackage sp = ub.getSourcePackages().get(0);
		System.out.println("The first one is " + sp.getName());
		System.out.println("It’s first file is " + sp.getFiles().get(0));
		
		/*
		System.out.println("and "
				+ ub.getBinaryPackages().size()
				+ " binary packages.");
		*/
	}
}
