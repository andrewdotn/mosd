package net.subjoin.mosd;

public class ArchiveInspector {
    
    static { System.loadLibrary("mosd"); }
    
    private ArchiveInspector() {};

    public static DistributionFile[] getContents(String path)
    throws ArchiveInspectorException
    {
	return getContents(path, new ArchiveInspectorErrorHandler() {
	    public @Override void handleError(String message)
	    throws ArchiveInspectorException
	    {
		throw new ArchiveInspectorException(message);
	    }
	});
    }
    
    /* FileNotFoundException is thrown if path is invalid. Otherwise the
     * errorHandler is called. */
    public static native DistributionFile[] getContents(String path,
	    ArchiveInspectorErrorHandler errorHandler)
    throws ArchiveInspectorException;
}
