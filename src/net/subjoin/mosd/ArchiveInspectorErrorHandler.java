/**
 * 
 */
package net.subjoin.mosd;

public interface ArchiveInspectorErrorHandler {
    /* Throwing an exception aborts the archive inspection. */ 
    public void handleError(String message)
    throws ArchiveInspectorException;
}