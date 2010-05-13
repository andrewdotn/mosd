package net.subjoin.mosd;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UbuntuDistributionTest {

    private UbuntuDistribution _ub;
    private List<SourcePackage> _spl;
    private SourcePackage _libbar;
    private SourcePackage _aprog;
    private TestFile _basePath;

    public @Before void setUp()
    throws IOException
    {
	_basePath = Util.loadTestFile("mockbuntu");
	_ub = new UbuntuDistribution(_basePath.getPath(), "tasty");
	_spl = _ub.getSourcePackages();
	_aprog = _spl.get(0);
	_libbar = _spl.get(1);
    }
    
    public @Test void basicTest() {
	assertEquals(2, _spl.size());
	assertEquals("aprog", _aprog.getName());
	assertEquals("libbar", _libbar.getName());
    }
    
    public @Test void testFileSize() {
	assertEquals(12, _aprog.getUncompressedFileCount());
	assertEquals(120, _aprog.getUncompressedSize());
    }
    
    public @Test void testFileSizeWithRecursion() {
	assertEquals(24, _libbar.getUncompressedFileCount());
	assertEquals(240, _libbar.getUncompressedSize());
    }
    
    public @After void tearDown() {
	_basePath.close();
    }
}
