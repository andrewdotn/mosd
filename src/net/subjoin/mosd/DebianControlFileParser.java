package net.subjoin.mosd;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class DebianControlFileParser
extends AbstractRecDescentParser
implements Iterable<DebianControlFile>
{
	public DebianControlFileParser(String string) {
		init(string);
	}
	
	public DebianControlFileParser(File file)
	throws IOException
	{
		this(Util.fileToStringMaybeGz(file));
	}

	public DebianControlFile controlFile() {
	    DebianControlFile r = new DebianControlFile();
	    
	    while (!eof() && !lookahead('\n')) {
		String keyword = keyword();
		
		match(':');
		if (lookahead(' '))
		    match(' ');
		else {
		    match('\n');
		    match(' ');
		}
		    
		String description = description();
		r.add(keyword, description);
	    }
	    if (!eof())
		match('\n');
	    return r;
	}
	
	private String description() {
	    StringBuilder sb = new StringBuilder();
	    while (!lookahead('\n'))
		sb.append(match());
	    match('\n');
	    if (lookahead(' ')) {
		match();
		sb.append('\n');
		sb.append(description());
	    }
	    return sb.toString();
	}
	
	private String keyword() {
	    StringBuilder sb = new StringBuilder();
	    while (!lookahead(':'))
		sb.append(match());
	    return sb.toString();
	}

	/* For things like packages.gz that have multiple entries */
	public Iterator<DebianControlFile> controlFiles() {
	    return new Iterator<DebianControlFile>() {
		public boolean hasNext() {
		    return !eof();
		}
		
		public DebianControlFile next() {
		    return controlFile();
		}
		
		public void remove() {
		    throw new UnsupportedOperationException();
		}
	    };
	}
	
	public Iterator<DebianControlFile> iterator() {
	    return controlFiles();
	}
}
