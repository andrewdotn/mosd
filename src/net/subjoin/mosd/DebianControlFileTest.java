package net.subjoin.mosd;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class DebianControlFileTest {

    public @Test void testFileHandling()
    throws IOException
    {
	String packages1 = Util.testFileAsString("filetest.txt");
	DebianControlFileParser p = new DebianControlFileParser(packages1);
	DebianControlFile dcs1 = p.controlFile();

	{
        	List<DistributionFile> files = dcs1.getFiles();
        	assertEquals(2, files.size());
        	assertEquals(8292584, files.get(0).getSize());
        	assertEquals("main/binary-amd64/Packages",
        		files.get(0).getFile().getPath());
	}
	
	DebianControlFile dcs2 = p.controlFile();
	
	{
        	List<DistributionFile> files = dcs2.getFiles();
        	assertEquals(3, files.size());
        	assertEquals(1195, files.get(0).getSize());
        	assertEquals(new File("foo"), files.get(0).getDirectory());
        	assertEquals("foo/aalib_1.4p5-38.dsc",
        		files.get(0).getFile().getPath());
	}
    }
}
