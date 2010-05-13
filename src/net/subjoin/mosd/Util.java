package net.subjoin.mosd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;


public class Util {

    	private static int BUFFER_SIZE = 0x40000; /* 256KiB */
	
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
			char[] buf = new char[BUFFER_SIZE];
			int r;
			while ((r = reader.read(buf)) != -1) {
				sb.append(buf, 0, r);
			}
			return sb.toString();
		} finally {
			s.close();
		}
	}
	
	public static String testFileAsString(String path)
	{
	    try {
		return stringFromStream(Util.class.getResourceAsStream(path));
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	}
	
	public static TestFile testFile(String path) {
	    return new TestFile(
		    new File(Util.class.getResource(path).getPath()));
	}
	
	public static TestFile tempFile()
	{
	    return tempFile(null);
	}
	
	public static TestFile tempFile(String suffix)
	{
	    try {
        	    final File tempFile = File.createTempFile(
        		    Util.class.getPackage().getName(), suffix);
        	    return new TestFile(tempFile) {
        		
        		public @Override void close() {
        		    tempFile.delete();
        		}
        	    };
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }        	    
	}

	public static TestFile tempFileContaining(String string) {
	    try {
        	    TestFile file = tempFile();
        	    FileOutputStream os = new FileOutputStream(file.getFile());
        	    os.write(string.getBytes());
        	    return file;
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	}
}
