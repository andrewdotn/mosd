package net.subjoin.mosd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
}
