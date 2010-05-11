package net.subjoin.mosd;

public class Main {
	
	public static void main(String[] args) {
		UbuntuDistribution ub = new UbuntuDistribution(args[0]);
		
		System.out.println("The distribution at " + ub.getPath());
		
		System.out.println(" has source package metadata files at:");	
		for (Object o: ub.getSourcePackageMetadataFiles())
			System.out.println("\t" + o);

		/*
		System.out.println("and binary package metadata files at:");
		for (Object o: ub.getBinaryPackageMetadataFiles())
			System.out.println("\t" + o):
		*/
		
		/*
		System.out.println("I know about "
				+ ub.getSourcePackages.size()
				+ " source packages and "
				+ ub.getBinaryPackages.size()
				+ " binary packages.");
		*/
	}
}
