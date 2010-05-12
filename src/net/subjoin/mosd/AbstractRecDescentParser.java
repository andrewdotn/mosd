package net.subjoin.mosd;

public abstract class AbstractRecDescentParser {
	protected Object EOF = new Object();

	private String _str;
	private int _index;
	
	private final int ERRMSG_CONTEXT_CHARS = 40;
	
	protected void init(String str) {
		_str = str;
		_index = 0;
	}
	
	protected boolean eof() {
		return _index >= _str.length();
	}
	
	protected boolean lookahead(char c) {
		if (_index < _str.length())
			return _str.charAt(_index) == c;
		/* no warning if at EOF */
		if (_index > _str.length())
			throw new RuntimeException("Tried to read past end.");
		return false;
	}
	
	protected char lookahead() {
		if (_index < _str.length())
			return _str.charAt(_index);
		throw new RuntimeException("Tried to read past end.");
	}
	
	protected char match() {
		char c = lookahead();
		match(c);
		return c;
	}
	
	protected void match(char c) {
		if (lookahead() == c)
			_index += 1;
		else
			raiseSyntaxException("Asked to match ‘" + c + "’");
	}
	
	private void raiseSyntaxException(String msg) {
		StringBuilder err = new StringBuilder(msg);
		err.append(" at char " + (_index + 1) + ": ");
		int lowIndex = Math.max(0, _index - ERRMSG_CONTEXT_CHARS);
		int highIndex = Math.min(_str.length(), _index + ERRMSG_CONTEXT_CHARS);
		err.append(_str.substring(lowIndex, _index));
		err.append("[ERROR->");
		err.append(_str.substring(_index, highIndex));
		throw new ParseException(err.toString());
	}
}

