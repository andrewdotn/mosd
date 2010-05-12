package net.subjoin.mosd;


public class ArchiveInspector {
    
    static { System.loadLibrary("mosd"); }
    
    private ArchiveInspector() {};

    public static native DistributionFile[] getContents(String path)
    throws ArchiveInspectorException;
    
}
