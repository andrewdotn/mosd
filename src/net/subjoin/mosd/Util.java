package net.subjoin.mosd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

public class Util {
	private static int ONE_MEGABYTE = 0x100000;
	
	static String fileToStringMaybeGz(File file)
	throws IOException
	{
		InputStream is;
		
		if (file.getName().endsWith(".gz")) {
			is = new GZIPInputStream(new FileInputStream(file));
		} else {
			is = new FileInputStream(file);
		}
		return stringFromStream(is);
	}
	
	private static String stringFromStream(InputStream s)
	throws IOException
	{
		Reader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(s));
			
			StringBuilder sb = new StringBuilder();
			char[] buf = new char[ONE_MEGABYTE];
			int r;
			while ((r = reader.read(buf)) != -1) {
				sb.append(buf, 0, r);
			}
			return sb.toString();
		} finally {
			s.close();
		}
	}
	
	static String stringResource(Class<?> cls, String path)
	throws IOException
	{
		return stringFromStream(cls.getResourceAsStream(path));
	}
}
