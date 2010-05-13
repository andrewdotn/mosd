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
	
	public static String getTestFileAsString(String path)
	{
	    try {
		return stringFromStream(
			new FileInputStream(loadTestFile(path).getFile()));
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	}
	
	public static TestFile loadTestFile(String path) {
	    File classFile = new File(
		    Util.class.getResource("Util.class").getPath());
	    int ndots = Util.class.getName().split("\\.").length;
	    for (int i = 0; i < ndots + 1; i++) {
		classFile = classFile.getParentFile();
	    }
	    return new TestFile(
		    new File(classFile.getPath() + "/testdata/" + path));
	}
	
	public static TestFile createTempFile()
	{
	    return createTempFile(null);
	}
	
	public static TestFile createTempFile(String suffix)
	{
	    return createTempFileContaining("", suffix);
	}

	public static TestFile createTempFileContaining(
		String string, String suffix)
	{
	    final File tempFile;
	    try {
        	    tempFile = File.createTempFile(
        		    Util.class.getPackage().getName(), suffix);
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }

	    try {
		if (string.length() > 0) {
		    FileOutputStream os = new FileOutputStream(tempFile);
		    os.write(string.getBytes());
		    os.close();
                }
	    } catch (IOException e) {
		tempFile.delete();
		throw new RuntimeException(e);
	    }
        	    
	    return new TestFile(tempFile) {
		public @Override void closeOverride() {
		    tempFile.delete();
		}
	    };
	}
}