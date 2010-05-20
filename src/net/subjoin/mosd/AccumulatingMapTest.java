package net.subjoin.mosd;

import org.junit.Test;
import static org.junit.Assert.*;

public class AccumulatingMapTest {

    public @Test void testBasics() {
	AccumulatingMap<String> map = AccumulatingMap.create();

	map.add("foo", 12);
	assertEquals(12, map.get("foo"));
	
	map.add("foo", 13);
	assertEquals(25, map.get("foo"));
	
	map.increment("bar");
	assertEquals(1, map.get("bar"));
	
	map.increment("foo");
	assertEquals(26, map.get("foo"));
	
	assertEquals(2, map.keys().size());
	
	assertEquals(1, map.getTopEntries(1).size());
	assertEquals("foo", map.getTopEntries(1).get(0).getKey());
	long v = map.getTopEntries(1).get(0).getValue();
	assertEquals(26, v);
	
	assertEquals("foo", map.getTopEntries(2).get(0).getKey());
	assertEquals("bar", map.getTopEntries(2).get(1).getKey());
	
	map.add("foo", 10000000000L);
	assertEquals(10000000026L, map.get("foo"));
    }
}
