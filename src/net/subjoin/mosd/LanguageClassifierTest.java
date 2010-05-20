package net.subjoin.mosd;

import static net.subjoin.mosd.LanguageClassifier.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class LanguageClassifierTest {
    public @Test void testGetExtension() {
	assertEquals(".foo", getExtension("bar.foo"));
	assertEquals("bar", getExtension("foo/bar"));
	assertEquals(".txt", getExtension("foo/bar/bar.txt"));
    }
    
    public @Test void testGetDoubleExtension() {
	assertEquals("foo", getDoubleExtension("foo"));
	assertEquals(".foo.txt", getDoubleExtension("bar.foo.txt"));
	assertEquals("bar.gz", getDoubleExtension("foo/bar.gz"));
	assertEquals(".txt.in", getDoubleExtension("foo/bar/bar.txt.in"));
	assertEquals(".c.d", getDoubleExtension("foo/bar/a.b.c.d"));
    }
    
    public @Test void testStripExtension() {
	assertEquals("foo", stripExtension("foo.bar"));
	assertEquals("foo", stripExtension("qux/foo.bar"));
	assertEquals("foo", stripExtension("qux.d/foo.bar"));
	assertEquals("foo", stripExtension("qux.d/foo"));
	assertEquals("foo.2", stripExtension("qux.d/foo.2.gz"));
    }
    
    public @Test void testClassifyInFiles() {
	LanguageClassifier cl = new LanguageClassifier();
	assertEquals("C", cl.classifyFile(new DistributionFile("foo.c.in", 12)));
	assertEquals("unknown", cl.classifyFile(new DistributionFile("blah.azerty.in", 12)));
	assertTrue(cl.getUnknownExtensions().contains(".azerty.in"));
    }
}
