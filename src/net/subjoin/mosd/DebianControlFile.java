package net.subjoin.mosd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DebianControlFile {
    
    private List<String> _keys = new ArrayList<String>();
    private List<String> _values = new ArrayList<String>();
    
    Object entryCount() {
	return _keys.size();
    }

    List<String> getKeys() {
	return Collections.unmodifiableList(_keys);
    }

    List<String> getValues() {
	return Collections.unmodifiableList(_values);
    }

    void add(String keyword, String description) {
	_keys.add(keyword);
	_values.add(description);
    }
    
    public List<DistributionFile> getFiles() {
	List<String> paths = new ArrayList<String>();
	List<Long> sizes = new ArrayList<Long>();
	String directory = null;

	Set<String> seen = new TreeSet<String>();

	for (int i = 0; i < _keys.size(); i++) {
	    String key = _keys.get(i);
	    if (key.equals("Directory")) {
		directory = _values.get(i);
		continue;
	    }
	    
	    if (isFileKey(key)) {
		String[] lines = _values.get(i).split("\n");
		for (String line: lines) {
		    String[] lineParts = line.split("\\s+", 3); 
		    String path = lineParts[2];
		    if (seen.contains(path))
			continue;

		    sizes.add(Long.valueOf(lineParts[1]));
		    paths.add(lineParts[2]);
		    seen.add(lineParts[2]);
		}
	    }
	}

	List<DistributionFile> ret = new ArrayList<DistributionFile>();
	for (int i = 0; i < paths.size(); i++) {
	    ret.add(new DistributionFile(directory, paths.get(i), sizes.get(i)));
	}
	return ret;
    }
    
    private static final Map<String, String> HASH_KEYS;
    static {
	HashMap<String, String> hash = new HashMap<String, String>();
	hash.put("Files", "MD5");
	hash.put("MD5", "MD5");
	hash.put("SHA1", "SHA1");
	hash.put("SHA256", "SHA256");
	hash.put("Checksums-Md5", "MD5");
	hash.put("Checksums-Sha1", "SHA1");
	hash.put("Checksums-Sha256", "SHA256");
	HASH_KEYS = Collections.unmodifiableMap(hash); 
    }
    
    private boolean isFileKey(String key) {
	return HASH_KEYS.containsKey(key);
    }

    public String getKey(String string) {
	for (int i = 0; i < _keys.size(); i++)
	    if (string.equals(_keys.get(i)))
		return _values.get(i);
	return null;
    }
    
    public @Override String toString() {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < _keys.size(); i++)
	    sb.append(_keys.get(i) + ": " + _values.get(i) + "\n");
	return sb.toString();
    }
    
}
