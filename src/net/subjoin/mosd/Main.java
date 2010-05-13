package net.subjoin.mosd;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

public class Main {
	
	public static void main(String[] args)
	throws IOException
	{
		UbuntuDistribution ub = new UbuntuDistribution("ubuntu", "karmic");
		
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
		

		{
		    SourcePackage sp = ub.getSourcePackages().get(0);
		    List<DistributionFile> files = sp.getFiles();
		    for (DistributionFile df: files) {
			System.out.print("It has file " + df.getPath());
			if (new File(df.getPath()).exists()) {
			    System.out.println(" which has files " + 
				    Arrays.toString(ArchiveInspector.getContents(
					    df.getPath())));
			} else {
			    System.out.println(" .. which we don’t have a copy of.");
			}
		    }
		}
		
		totalBytes = 0;
		int i = 0;
		NumberFormat nf = NumberFormat.getInstance();
		for (SourcePackage sp: ub.getSourcePackages()) {
		    for (DistributionFile f: sp.getFiles())
			for (DistributionFile sf: ArchiveInspector.getContents(f.getPath()))
			    totalBytes += sf.getSize();
		    System.out.format("%s %d/%d, %s bytes\n",
			    sp.getName(), i + 1, ub.getSourcePackages().size(),
			    nf.format(totalBytes));
		    i++;
		}

	
		/*
		System.out.println("and "
				+ ub.getBinaryPackages().size()
				+ " binary packages.");
		*/
	}
}
