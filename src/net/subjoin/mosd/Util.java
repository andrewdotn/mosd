package net.subjoin.mosd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;


public class Util {
	public static interface TempFileUsingRunnable {
        void run(File file) throws IOException;
    }

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
	
	static String stringResource(Class<?> cls, String path)
	throws IOException
	{
		return stringFromStream(cls.getResourceAsStream(path));
	}

	public static void runWithTempFileContaining(String string,
	    Util.TempFileUsingRunnable r)
	throws IOException
	{
	    Util.runWithTempFileContaining(string, null, r);
	}

	public static void runWithTempFileContaining(String string, String suffix,
	    TempFileUsingRunnable r)
	throws IOException
	{
	    File tmpFile = null;
	    try {
	        tmpFile = File.createTempFile(UtilTest.class.getCanonicalName(),
	            suffix);
	
	        FileWriter writer = new FileWriter(tmpFile);
	        try {
	            writer.append(string);
	        } finally {
	            writer.close();
	        }
	
	        r.run(tmpFile);
	
	    } finally {
	        if (tmpFile != null)
	            tmpFile.delete();
	    }		
	}
	

	public static void runWithTempFileContaining(byte[] bytes, String suffix,
	    Util.TempFileUsingRunnable r)
	throws IOException
	{
	    File tmpFile = null;
	    try {
	        tmpFile = File.createTempFile(UtilTest.class.getCanonicalName(),
	            suffix);
	
	        OutputStream out = new FileOutputStream(tmpFile);
	        try {
	            out.write(bytes);
	        } finally {
	            out.close();
	        }
	
	        r.run(tmpFile);
	
	    } finally {
	        if (tmpFile != null)
	            tmpFile.delete();
	    }
	}
	

	public static void runWithTempFileContainingResource(
		Class<?> cls, String resourceName,
		String suffix, Util.TempFileUsingRunnable tempFileUsingRunnable)
	throws IOException
	{
	    InputStream is = cls.getResourceAsStream(resourceName);
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int size = 0, bytesRead;
	    while ((bytesRead = is.read(buffer, size, buffer.length - size)) != -1) {
		size += bytesRead;

		byte[] newLargerBuffer = new byte[size + BUFFER_SIZE];
		System.arraycopy(buffer, 0, newLargerBuffer, 0, size);
		buffer = newLargerBuffer; 
	    }
	    buffer = Arrays.copyOf(buffer, size);

	    Util.runWithTempFileContaining(buffer, suffix, tempFileUsingRunnable);
	}
}
