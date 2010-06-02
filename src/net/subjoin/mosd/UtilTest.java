package net.subjoin.mosd;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

public class UtilTest {

    public @Test void testLoadResource()
    throws IOException
    {
        assertEquals("Hello, world!\nThis is a test.\n",
            Util.getTestFileAsString("test.txt"));
    }

    public @Test void testFileToString()
        throws IOException
    {
        final String testString = "Hello, world!\n";
        
        TestFile testFile = Util.loadTestFile("hello.txt");
        try {
            assertEquals(testString, Util.fileToStringMaybeGz(testFile.getFile()));
        } finally {
            testFile.close();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            sb.append(testString);
        }
        final String testString2 = sb.toString();
        /* Make sure we exceed the buffer size of the reader method */
        assertTrue(testString2.length() >  0x100000);
        
        TestFile tmpFile = Util.createTempFileContaining(testString2, "");
        try {
            assertEquals(testString2, Util.fileToStringMaybeGz(tmpFile.getFile()));
        } finally {
            tmpFile.close();
        }
        assertFalse(tmpFile.getFile().exists());

        TestFile file = Util.loadTestFile("hello.txt.gz");
        try {
            assertEquals(testString, Util.fileToStringMaybeGz(file.getFile()));
        } finally {
            file.close();
        }
    }
    
    public @Test void testChoose() {
	List<String> l1 = Arrays.asList("a", "b", "c", "d", "e", "f", "g");
	List<String> r1 = Util.choose(l1, l1.size() - 1, 0);
	List<String> r2 = Util.choose(l1, l1.size() - 1, 0);
	assertFalse(r1.get(0).equals(l1.get(0)));
	assertEquals(r1, r2);
	assertEquals(r1.size(), l1.size() - 1);
	List<String> l2 = new ArrayList<String>(l1);
	l2.removeAll(r1);
	assertEquals(l2.size(), 1);
    }
    
    public @Test void testChooseWeighted() {
	List<Integer> l1 = Arrays.asList(0, 0, 0, 0, 1);
	int sum = 0;
	for (int i = 0; i < 1000; i++) {
	    sum += Util.choose(l1, 1, i).get(0);
	}
	assertTrue(sum > 180);
	assertTrue(sum <= 220);
	
	List<Integer> l2 = new ArrayList<Integer>(1000);
	for (int i = 0; i < 1000; i++)
	    l2.add(i / 100);
	l2 = Util.choose(l2, 9, 0);
	assertEquals(l2.size(), new HashSet<Integer>(l2).size()); 
    }
    
    @Test(expected=RuntimeException.class)
    public void testChooseUnsatisfiable() {
	List<String> l1 = Arrays.asList("a", "a", "b", "b");
	Util.choose(l1, 3, 0);
    }
}
