package net.subjoin.mosd;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Map;

/* horrible hack—classloader stuff means analyzer class has to end in “Analyzer” */
public class ReloaderDriver {
    public ReloaderDriver()
    throws Exception
    {
    }
    
    public static void main(String... args)
    throws IOException
    {
	long timestamp = System.currentTimeMillis();
	UbuntuDistribution ub = new UbuntuDistribution("../ubuntu", "karmic");
	Map<String, SourcePackage> sp = Main.loadCache("tasty.cache.gz");
	System.out.format("%,d millis\n", System.currentTimeMillis() - timestamp);
	System.out.println("ready");
	BufferedReader reader = new BufferedReader(
		new InputStreamReader(System.in));
	String line;
	while ((line = reader.readLine()) != null) {
	    Class<?> c = new Reloader().loadClass(
		    Reloader.class.getPackage().getName() + "."
		    + line.trim());
	    
	    try {
		timestamp = System.currentTimeMillis();
		c.getConstructor(UbuntuDistribution.class,
			Map.class).newInstance(ub, sp);
		System.out.format("%,d millis\n", System.currentTimeMillis() - timestamp);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
}

// http://stackoverflow.com/questions/3971534/how-to-force-java-to-reload-class-upon-instantiation
class Reloader extends ClassLoader {
    public static void main(String[] args) throws Exception {
        do {
            Object foo = new Reloader().loadClass("MyFoo").newInstance();
            System.out.println("LOADED: " + foo); // Overload MyFoo#toString() for effect
            System.out.println("Press <ENTER> when MyFoo.class has changed");
            System.in.read();
        } while (true);
    }

    @Override
    public Class<?> loadClass(String s) {
        return findClass(s);
    }

    @Override
    public Class<?> findClass(String s) {
	if (s.startsWith(ReloaderDriver.class.getPackage().getName() + ".")
		&& !s.endsWith("Analyzer"))
	{
	    try {
		return super.loadClass(s);
	    } catch (ClassNotFoundException e) {
		throw new RuntimeException(e);
	    }
	}
	    
        try {
            byte[] bytes = loadClassData(s);
            return defineClass(s, bytes, 0, bytes.length);
        } catch (IOException ioe) {
            try {
                return super.loadClass(s);
            } catch (ClassNotFoundException ignore) { }
            ioe.printStackTrace(System.out);
            return null;
        }
    }

    private byte[] loadClassData(String className)
    throws IOException
    {
        File f = new File("bin/" + className.replaceAll("\\.", "/") + ".class");
        int size = (int) f.length();
        byte buff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        dis.readFully(buff);
        dis.close();
        return buff;
    }
}