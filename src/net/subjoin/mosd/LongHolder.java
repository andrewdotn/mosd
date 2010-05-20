package net.subjoin.mosd;

public class LongHolder {
    private long _value;
    
    public LongHolder() {
	_value = 0;
    }
    
    public long getValue() {
	return _value;
    }
    
    public void setValue(long value) {
	_value = value;
    }
    
    public long add(long value) {
	return _value += value;
    }

    public long increment() {
	return ++_value;
    }
    
}
