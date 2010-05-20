package net.subjoin.mosd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

public class AccumulatingMap<E>
{
    private Map<E, LongHolder> _count;
    private long _total;
    
    public AccumulatingMap() {
	_count = Maps.newHashMap();
	_total = 0;
    }
    
    public long get(E e) {
	LongHolder h = _count.get(e);
	if (h == null)
	    throw new NoSuchElementException(e.toString());
	return h.getValue();
    }
    
    public Collection<E> keys() {
	return Collections.unmodifiableCollection(_count.keySet());
    }    
    
    public void add(E e, long count) {
	LongHolder h = _count.get(e);
	if (h == null) {
	    h = new LongHolder();
	    _count.put(e, h);
	}
	h.add(count);
	_total += count;
    }
    
    public void increment(E e) {
	LongHolder h = _count.get(e);
	if (h == null) {
	    h = new LongHolder();
	    _count.put(e, h);
	}
	h.increment();
	_total++;
    }
    
    public static <T> AccumulatingMap<T> create() {
	return new AccumulatingMap<T>();
    }

    public List<Entry<E, Long>> getTopEntries(int limit) {
	Collection<E> keys = keys();
	List<Entry<E, Long>> ret = new ArrayList<Entry<E,Long>>(keys.size());
	for (E e: keys)
	    ret.add(new SimpleImmutableEntry<E, Long>(e, get(e)));
	Collections.sort(ret, new Comparator<Entry<E,Long>>() {
	    public @Override int compare(Entry<E, Long> a, Entry<E, Long> b) {
		return Long.signum(b.getValue() - a.getValue());
	    }
	});
	limit = Math.min(limit, ret.size());
	return new ArrayList<Entry<E, Long>>(ret.subList(0, limit));
    }
    
    public long getTotal() {
	return _total;
    }
}