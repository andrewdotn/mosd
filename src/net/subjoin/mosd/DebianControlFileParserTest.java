package net.subjoin.mosd;

import java.io.IOException;
import static org.junit.Assert.*;
import java.util.List;

import org.junit.Test;


public class DebianControlFileParserTest {
    
    public @Test void testKeyValue()
    throws IOException
    {
	String packages1 = Util.getTestFileAsString("package1.txt");
	DebianControlFileParser p = DebianControlFileParser.fromString(packages1);
	DebianControlFile dcs = p.controlFile();
	
	assertEquals(3, dcs.entryCount());
	List<String> keys = dcs.getKeys();
	List<String> values = dcs.getValues();
	
	assertEquals("Name", keys.get(0));
	assertEquals("foo", values.get(0));
	assertEquals("Foo", keys.get(1));
	assertEquals("bar", values.get(1));
	assertEquals("Desc", keys.get(2));
	assertEquals("This\nis\na\n.\ntest.", values.get(2));
    }
    
    public @Test void testMultipleEntries()
    throws IOException
    {
	String packages1 = Util.getTestFileAsString("packages1.txt");
	DebianControlFileParser p = DebianControlFileParser.fromString(packages1);
	
	int i = 0;
	for (DebianControlFile dcs: p) {
	    if (i == 0) {
		assertEquals("Foo", dcs.getKeys().get(0));
		assertEquals("Bar", dcs.getValues().get(0));
	    } else if (i == 1) {
		assertEquals("Bar", dcs.getKeys().get(0));
		assertEquals("Baz", dcs.getValues().get(0));
	    }
	    i++;
	}
    }
}