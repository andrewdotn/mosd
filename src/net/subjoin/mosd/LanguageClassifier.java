package net.subjoin.mosd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

public class LanguageClassifier {
    
    private static final Map<String,String> EXTENSION_MAP
    	= ImmutableMap.<String, String>builder()
        	.put(".c", "C")
        	.put(".h", "C")
        	.put(".H", "C")
        	.put(".hin", "C")
        	.put(".cxx", "C")
        	.put(".cpp", "C")
        	.put(".cc", "C")
        	.put(".hpp", "C")
        	.put(".hxx", "C")
        	.put(".C", "C")
        	.put(".hh", "C")
        	.put(".i", "C")
        	.put(".c++", "C")
        	.put(".sh", "sh")
        	.put(".awk", "awk")
        	.put(".y", "yacc")
        	.put(".ada", "ada")
        	.put(".adb", "ada")
        	.put(".ads", "ada")
        	.put(".asm", "assembly")
        	.put(".S", "assembly")
        	.put(".s", "assembly")
        	.put(".java", "java")
        	.put(".py", "python")
        	.put(".f", "fortran")
        	.put(".F", "fortran")
        	.put(".f90", "fortran")
        	.put(".F90", "fortran")
        	.put(".pm", "perl")
        	.put(".pl", "perl")
        	.put(".PL", "perl")
        	.put(".sql", "SQL")
        	.put(".php", "PHP")
        	.put(".phpt", "PHP")
        	.put(".js", "JavaScript")
        	.put(".xsl", "XSL")
        	.put(".el", "elisp")
        	.put(".cs", "csharp")
        	.put(".lisp", "lisp")
        	.put(".lsp", "lisp")
        	.put(".sty", "TeX")
        	.put(".dtx", "TeX")
        	.put(".rb", "ruby")
        	.put(".pas", "pascal")
        	.put(".tcl", "TCL")
        	.put(".scm", "scheme")
        	.put(".hs", "Haskell")
        	.put(".R", "R")
        	.put(".lua", "lua")
        	.put(".scala", "scala")
        	.put(".boo", "boo")
        	.put(".mf", "MetaFont")
        	.put(".sed", "sed")
        	.put(".groovy", "groovy")
        	.put(".m", ".m")
        	.put(".ml", "ML")
        	.put(".sml", "ML")
        	.put(".mli", "ML")
        	.put(".l", "lex")
        	.put(".ll", "lex")
        	.put(".bat", "DOS batch file")
        	// scilab OR Space Quest game script :/
        	.put(".sci", ".sci")
        	.put(".vb", "visual basic")
        	.put(".pike", "pike")
        	.put(".erl", "erlang")
        	
        	.put(".ac", "build")
        	.put(".am", "build")
        	.put(".mk", "build")
        	.put("Makefile", "build")
        	.put(".mak", "build")
        	.put("makefile", "build")
        	.put("rules", "build")
        	.put("GNUmakefile", "build")
        	.put(".make", "build")
        	.put("Imakefile", "build")
        	.put(".spec", "build")
        	.put(".jam", "build")
        	.put(".guess", "build")
        	.put("configure", "build")
        	.put("depcomp", "build")
        	.put("install-sh", "build")
        	.put("missing", "build")
        	.put(/*config*/".sub", "build")
        	.put(".m4", "build")
   	        .put(".cmake", "build")
   	        .put(".pro", "build")
        	
                .put("THANKS", "other")
                .put(".txt", "other")
                .put(".TXT", "other")
                .put(".po", "other")
                .put(".gmo", "other")
                .put(".pot", "other")
                .put(".mo", "other")
                .put("ChangeLog", "other")
                .put("Changelog", "other")
                .put(".tex", "other")
                .put(".info", "other")
                .put(".texi", "other")
                .put(".texinfo", "other")
                .put("COPYING", "other")
                .put("INSTALL", "other")
                .put("TODO", "other")
                .put("README", "other")
                .put(".man", "other")
                .put(".1", "other")
                .put(".2", "other")
                .put(".3", "other")
                .put(".4", "other")
                .put(".5", "other")
                .put(".6", "other")
                .put(".7", "other")
                .put(".8", "other")
                .put(".9", "other")
                .put("ABOUT-NLS", "other")
                .put("NEWS", "other")
                .put("AUTHORS", "other")
                .put("VERSION", "other")
                .put("PORTS", "other")
                .put(".png", "other")
                .put(".html", "other")
                .put(".jpg", "other")
                .put(".gif", "other")
                .put(".xpm", "other")
                .put("LICENSE", "other")
                .put("BUGS", "other")
                .put(".odt", "other")
                .put(".ppt", "other")
                .put(".css", "other")
                .put(".wav", "other")
                .put(".svg", "other")
                .put("CHANGES", "other")
                .put(".tga", "other")
                .put(".ogg", "other")
                .put(".pdf", "other")
                .put(".bmp", "other")
                .put(".eps", "other")
                .put(".ttf", "other")
                .put(".so", "other")
                .put(".htm", "other")
                .put(".docbook", "other")
                .put(".svgz", "other")
                .put(".bdf", "other")
                .put(".pfb", "other")
                .put(".ppd", "other")
                .put(".pdb", "other")
                .put(".svn-base", "other")
                .put(".afm", "other")
                .put(".tiff", "other")
                .put(".mp3", "other")
                .put(".dll", "other")
                .put(".tif", "other")
                .put(".ico", "other")
                .put(".tfm", "other")
                .put(".mpg", "other")
                .put(".xbm", "other")
                .put(".csv", "other")
                .put(".otf", "other")
                .put(".pod", "other")
                .put(".bsp", "other")
                .put(".psd", "other")
                .put("Entries", "other")
                .put("Repository", "other")
                .put("Root", "other")
                .put("Tag", "other")
                .put(".swf", "other")
                .put(".dxf", "other")
                .put(".class", "other")
                .put(".dot", "other")
                .put(".sgml", "other")
                .put(".vcprog", "other")
                .put("changelog", "other")
                .put("copyright", "other")
                .put("control", "other")
                .put("MANIFEST", "other")
                .put("PORTING", "other")
                .put("Change", "other")
                .put(".cvsignore", "other")
                .put(".gitignore", "other")
                .put(".pyc", "other")
                .put("COPYRIGHT", "other")
                .put(".txt,v", "other")
                .put(".jpeg", "other")
                .put("CHANGELOG", "other")
                .put("FAQ", "other")
                .put("FILES", "other")
                .put(".elc", "other")	
                .put(".strings", "other")
                // huge test files from star
                .put("8gb-1", "other")
                .put("big", "other")
                // bzr repository files
                .put(".kndx", "other")
                .put(".knit", "other")
                .put(".md5", "other")
                .put(".vcproj", "other")
                .put(".ui", "other")
                .put(".desktop", "other")
                // gimp files
                .put(".xcf", "other")
                .put(".cache", "other")
                .put(".conf", "other")
                .put(".properties", "other")
                .put(".yaml", "other")
                .put(".sdf", "other")
                // man page section for R
                .put(".Rd", "other")
                .put(".svn-work", "other")
                .put(".rst", "other")
                // GSM-encoded sound files
                .put(".gsm", "other")
                .put(".vf", "other")
                .put(".ics", "other")
                .put(".rgb", "other")
                .put(".pk3", "other")
                .build();
    
    private AccumulatingMap<String> _classCounts = AccumulatingMap.create();
    private AccumulatingMap<String> _classBytes = AccumulatingMap.create();
    private AccumulatingMap<String> _unknownExtensionCounts
    	= AccumulatingMap.create();
    private AccumulatingMap<String> _unknownExtensionBytes
    	= AccumulatingMap.create();
   
                    
    public void classify(Iterator<DistributionFile> itdf) {
	while (itdf.hasNext()) {
	    DistributionFile df = itdf.next();
	    classifyFile(df);
	}
    }
    
    public String classifyFile(DistributionFile df) {
	    String extension = getExtension(df.getPath());
	    String doubleExtension = null;
	    
	    if (extension.equals(".in")
		    || extension.equals(".bz2") || extension.equals(".gz")) {
		doubleExtension = getDoubleExtension(df.getPath());
		extension = stripExtension(doubleExtension);
	    }
	    
	    String category = EXTENSION_MAP.get(extension);
	    if (category == null) {
		if (doubleExtension != null)
		    extension = doubleExtension;
		category = "unknown";
		_unknownExtensionCounts.increment(extension);
		_unknownExtensionBytes.add(extension, df.getSize());
	    } else {
		_classCounts.increment(category);
		_classBytes.add(category, df.getSize());
	    }
	    return category;
    }
    
    public String getTopUnknown() {
	return getTopUnknown(10);
    }
    
    /* For binary classifications only! */
    private <E> String ratio(AccumulatingMap<E> classOne,
	    AccumulatingMap<E> classTwo)
    {
	long a = classOne.getTotal();
	long b = classTwo.getTotal();
	long t = a + b;
	return String.format("%,d/%,d %.0f%%",
		a, t, 100.0 * a / t);
    }

    public String getTopUnknown(int limit) {
	Formatter f = new Formatter();
	f.format("Top unknown by file count (total %s)\n",
		ratio(_unknownExtensionCounts, _classCounts));
		
	for (Map.Entry<String, Long> e:
	    _unknownExtensionCounts.getTopEntries(limit))
	    f.format("%,16d %,16d %s\n", e.getValue(), 
		    _unknownExtensionBytes.get(e.getKey()),
		    e.getKey());
	f.format("\nTop unknown by bytes (total %s):\n",
		ratio(_unknownExtensionBytes, _classBytes));
	for (Map.Entry<String, Long> e:
	    _unknownExtensionBytes.getTopEntries(limit))
	    f.format("%,16d %,16d %s\n",
		    _unknownExtensionCounts.get(e.getKey()),
		    e.getValue(), 
		    e.getKey());
	return f.toString();
    }
    

    public String getLanuageClassification() {
	int limit = Integer.MAX_VALUE - 1;
	
	System.out.format("%s files, %s bytes unknown\n",
		ratio(_unknownExtensionCounts, _classCounts),
		ratio(_unknownExtensionBytes, _classBytes));
	Formatter f = new Formatter();
	f.format("Languages by count\n");
	for (Map.Entry<String, Long> e:
	    _classCounts.getTopEntries(limit))
	    f.format("%,16d %,16d %s\n", e.getValue(), 
		    _classBytes.get(e.getKey()),
		    e.getKey());
	
	f.format("\nLanguages by bytes\n");
	for (Map.Entry<String, Long> e:
	    _classBytes.getTopEntries(limit))
	    f.format("%,16d %,16d %s\n",
		    _classCounts.get(e.getKey()),
		    e.getValue(), 
		    e.getKey());
	return f.toString();
    }
    
    public Collection<String> getUnknownExtensions()
    {
	return _unknownExtensionCounts.keys();
    }
    
    static String getBasename(String path) {
	int index = path.lastIndexOf('/');
	if (index < 0)
	    return path;
	else
	    return path.substring(index + 1);
    }


   static String getExtension(String path) {
	String basename = getBasename(path);
	
	String extension;
	int index = basename.lastIndexOf('.');
	if (index < 0)
	extension = basename;
	else
	extension = basename.substring(index);
	return extension;
    }
   
   static String getDoubleExtension(String path) {
	String basename = getBasename(path);
	
	int index = basename.lastIndexOf('.');
	if (index < 0)
	    return basename;
	
	index = basename.lastIndexOf('.', index - 1);
	if (index < 0)
	    return basename;
	return basename.substring(index);
   }

   static String stripExtension(String path) {
       String basename = getBasename(path);
       
       int index = basename.lastIndexOf('.');
       if (index < 0)
	   return basename;
       return basename.substring(0, index);
   }
   
   private static final Collection<String> EXCLUDED_LANGUAGES
   	= Arrays.asList("build", "other", "sh");
   public Collection<String> getLanguages() {
       Collection<String> c = new ArrayList<String>(_classCounts.keys());
       c.removeAll(EXCLUDED_LANGUAGES);
       return c;
   }

   public int getLanguageCount() {
       return getLanguages().size();
   }
   
   public static void main(String... args) {
       Map<String, List<String>> m2 = new HashMap<String, List<String>>();
       for (Map.Entry<String, String> e: EXTENSION_MAP.entrySet()) {
	   if (!m2.containsKey(e.getValue()))
	       m2.put(e.getValue(), new ArrayList<String>());
	   m2.get(e.getValue()).add(e.getKey());
       }
       
       for (String s: m2.keySet()) {
	   System.out.println(s + ": " + Joiner.on(", ").join(m2.get(s)));
	}
       
       System.out.println("Hello, world!");
       
   }
}