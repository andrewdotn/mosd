package net.subjoin.mosd;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class Main {
	
	public static void main(String[] args)
	throws IOException, ClassNotFoundException
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

		if (cacheFile.exists()) {
        		ObjectInputStream is = new ObjectInputStream(
				new GZIPInputStream(
        			new BufferedInputStream(
        				new FileInputStream(cacheFile))));
        		cache = Maps.newHashMap();
        		while (true) {
        		    try {
                		    SourcePackage sp = (SourcePackage)is.readObject();
                		    cache.put(sp.getName(), sp);
        		    } catch (EOFException e) {
        			// there’s no method to check if end-of-file
        			// has been reached, unfortunately. 
        			break;
        		    }
        		}
        		is.close();
		}
		
		List<SourcePackage> spl = new ArrayList<SourcePackage>(
			ub.getSourcePackages());
		
		if (cacheFile.exists()) {
        		for (int i = 0; i < spl.size(); i++) {
        		    String name = spl.get(i).getName();
        		    SourcePackage sp = cache.get(name);
        		    if (sp != null)
        			spl.set(i, sp);
        		}
		}
		
		totalBytes = 0;
		long totalFiles = 0;
		int i = 0;
//		Set<String> erroredOut = Sets.newHashSet();
//		NumberFormat nf = NumberFormat.getInstance();
//		File newCacheFile = new File(cacheFile.getName() + ".new");
//		ObjectOutputStream os = new ObjectOutputStream(
//			new BufferedOutputStream(
//				new GZIPOutputStream(
//				new FileOutputStream(newCacheFile))));
		for (SourcePackage sp: spl) {
		    try {
			totalBytes += sp.getUncompressedBytes();
			totalFiles += sp.getUncompressedFileCount();
			
			//os.writeObject(sp);
		    } catch (RuntimeException e) {
			System.err.println(e);
//			erroredOut.add(sp.getName());
		    }
		    
//		    System.out.format("%s %d/%d, %s files, %s bytes\n",
//			    sp.getName(), i + 1, spl.size(),
//			    nf.format(totalFiles),
//			    nf.format(totalBytes));
		    i++;
		}
//		os.close();
//		if (!newCacheFile.renameTo(cacheFile))
//		    throw new RuntimeException("couldn’t rename file");
//		
//		System.out.println("Errors on:");
//		for (String s: erroredOut)
//		    System.out.println("\t" + s);

	
		/*
		System.out.println("and "
				+ ub.getBinaryPackages().size()
				+ " binary packages.");
		*/
	}
}
