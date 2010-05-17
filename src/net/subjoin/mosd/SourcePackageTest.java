package net.subjoin.mosd;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class SourcePackageTest {

    public @Test void testGettingFileNames() 
    throws IOException
    {
	TestFile testFile = Util.loadTestFile("libbar-2.1.tar.gz");
	try {
	    SourcePackage sp = new SourcePackage("libbar",
		    new DistributionFile(testFile.getPath(),
			    testFile.getFile().length()),
		    new DistributionFile(testFile.getPath(),
			    testFile.getFile().length()));
	    assertEquals(48, sp.getUncompressedFileCount());
	    assertEquals(480, sp.getUncompressedBytes());

	    DistributionFile testDf = new DistributionFile(testFile.getPath(),
			    testFile.getFile().length());
	    testDf.scanIfArchive();
	    assertTrue(testDf.iterateLeaves().hasNext());

	    DistributionFile contents[]
	    	= Iterators.toArray(testDf.iterateLeaves(),
	    		DistributionFile.class);
	    
	    assertEquals("libbar-2.1/00.py", contents[0].getPath());
	    assertEquals(4,
		    contents[contents.length - 1].getFullPath().length);
	    
	    assertTrue(sp.iterateSourceFiles().hasNext());
	    assertEquals(48, Lists.newArrayList(sp.iterateSourceFiles()).size());
	    
	} finally {
	    testFile.close();
	}
    }
    
    public @Test void testSerialization() 
    throws IOException
    {
	TestFile testFile = Util.loadTestFile("libbar-2.1.tar.gz");
	TestFile tempFile = Util.createTempFile();
	try { try {
	    SourcePackage sp = new SourcePackage("libbar",
		    new DistributionFile(testFile.getPath(),
			    testFile.getFile().length()),
		    new DistributionFile(testFile.getPath(),
			    testFile.getFile().length()));
	    ObjectOutputStream os = new ObjectOutputStream(
		    new BufferedOutputStream(
			    new FileOutputStream(tempFile.getFile())));
	    os.writeObject(sp);
	    os.close();
	    
	    ObjectInputStream is = new ObjectInputStream(
		    new BufferedInputStream(
			    new FileInputStream(tempFile.getFile())));
	    SourcePackage ser = (SourcePackage)is.readObject();
	    is.close();
	    
	    assertTrue(Iterators.elementsEqual(sp.iterateSourceFiles(),
		    ser.iterateSourceFiles()));
	    
	    assertEquals(48, sp.getUncompressedFileCount());
	    assertEquals(48, ser.getUncompressedFileCount());

	    assertTrue(Iterators.elementsEqual(sp.iterateSourceFiles(),
		    ser.iterateSourceFiles()));
	    
	    os = new ObjectOutputStream(
		    new BufferedOutputStream(
			    new FileOutputStream(tempFile.getFile())));
	    os.writeObject(sp);
	    os.close();
	    
	    is = new ObjectInputStream(
		    new BufferedInputStream(
			    new FileInputStream(tempFile.getFile())));
	    ser = (SourcePackage)is.readObject();
	    is.close();
	    
	    assertTrue(Iterators.elementsEqual(sp.iterateSourceFiles(),
		    ser.iterateSourceFiles()));
	    
	    assertEquals(48, sp.getUncompressedFileCount());
	    assertEquals(48, ser.getUncompressedFileCount());

	    assertTrue(Iterators.elementsEqual(sp.iterateSourceFiles(),
		    ser.iterateSourceFiles()));	    

	} catch (ClassNotFoundException e) {
	    throw new RuntimeException(e);
	} finally {
	    testFile.close();
	} } finally {
	    tempFile.close();
	}
    }
    
    public @Test void testSerialization2()
    throws IOException, ClassNotFoundException
    {
	TestFile testFile = Util.loadTestFile("test-lots-of-files.tar.gz");
	TestFile tempFile = Util.createTempFile();
	try { try {
	    SourcePackage sp = new SourcePackage("foo",
		    new DistributionFile(testFile.getPath(),
			    testFile.getFile().length()));
	    
	    sp.getUncompressedBytes();
	    
	    ObjectOutputStream os = new ObjectOutputStream(
		    new FileOutputStream(tempFile.getFile()));
	    os.writeObject(sp);
	    os.close();
	    
	    ObjectInputStream is = new ObjectInputStream(
		    new FileInputStream(tempFile.getFile()));
	    SourcePackage ser = (SourcePackage)is.readObject();
	    is.close();
	    
	    ser.getUncompressedBytes();
	    
	    assertTrue(Iterators.elementsEqual(sp.iterateSourceFiles(),
		    ser.iterateSourceFiles()));	    
	    
	} finally {
	    tempFile.close();
	} } finally {
	    testFile.close();
	}
    }
}
