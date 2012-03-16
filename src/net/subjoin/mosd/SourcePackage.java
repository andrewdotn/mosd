package net.subjoin.mosd;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class SourcePackage
implements Serializable
{
    private static final long serialVersionUID = 1;

    private transient String _name;
    private transient ImmutableList<DistributionFile> _files;
    private transient boolean _walked;
    private transient int _uncompressedFileCount;
    private transient long _uncompressedBytes;
    
    public SourcePackage(DebianControlFile dsc)
    {
	_name = dsc.getKey("Package");
	_files = ImmutableList.copyOf(dsc.getFiles());
	_walked = false;
    }
    
    public SourcePackage(String name, DistributionFile... files)
    {
	_name = name;
	_files = ImmutableList.copyOf(files);
	_walked = false;
    }
    
    public List<DistributionFile> getFiles() {
	return _files;
    }
    
    public String getName() {
	return _name;
    }
    
    public int getUncompressedFileCount() {
	if (!_walked)
	    walk();
	return _uncompressedFileCount;
    }
    
    private void walk() {
	final int[] count = new int[1];
	final long[] size = new long[1];
	for (DistributionFile df: getFiles()) {
	    File f = new File(df.getPath());
	    if (!f.exists()) {
		if (!isUpstreamFile(df))
		    continue;
		throw new RuntimeException("missing file " + f);
	    }
	    try {
		df.scanIfArchive();
		walk(df.getContainedFiles(), count, size);
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	}
	_uncompressedFileCount = count[0];
	_uncompressedBytes = size[0];
	_walked = true;
    }
    
    public static boolean isUpstreamFile(DistributionFile df) {
	File f = new File(df.getPath());
	return !f.getName().endsWith(".dsc")
		&& !f.getName().endsWith(".diff.gz");

    }
    
    static void walk(DistributionFile[] dfs, int[] count, long[] size)
    {
	for (DistributionFile f: dfs) {
	    if (f.containsOtherFiles())
		walk(f.getContainedFiles(), count, size);
	    else {
		count[0]++;
		size[0] += f.getSize();
	    }
	}
    }
    
    public Iterator<DistributionFile> iterateSourceFiles() {
	List<Iterator<DistributionFile>> upstreamFiles = Lists.newArrayList();
	for (DistributionFile f: _files)
	    if (isUpstreamFile(f))
		upstreamFiles.add(f.iterateLeaves());
	
	return Iterators.concat(upstreamFiles.iterator());
    }
    
    public long getUncompressedBytes() {
	if (!_walked)
	    walk();
	return _uncompressedBytes;
    }

    private void writeObject(ObjectOutputStream out)
    throws IOException
    {
	out.defaultWriteObject();
	
	out.writeUTF(_name);
	out.writeInt(_files.size());
	for (int i = 0; i < _files.size(); i++)
	    writeDf(out, _files.get(i));
    }
    
    private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
    {
	in.defaultReadObject();
	
	_name = in.readUTF();
	int numfiles = in.readInt();
	
	DistributionFile[] files = new DistributionFile[numfiles];
	for (int i = 0; i < numfiles; i++)
	    files[i] = readDf(in);
	_files = ImmutableList.copyOf(files);

	_walked = false;
    }
    
    private void writeDf(ObjectOutputStream out, DistributionFile file)
    throws IOException
    {
	out.writeUTF(file.getPath());
	out.writeLong(file.getSize());
	DistributionFile[] children = file.getContainedFiles();
	if (children == null) {
	    out.writeInt(-1);
	    return;
	}
	out.writeInt(children.length);
	for (int i = 0; i < children.length; i++)
	    writeDf(out, children[i]);
    }
    
    private DistributionFile readDf(ObjectInputStream in)
    throws IOException
    {
	String path = in.readUTF();
	long size = in.readLong();
	int numChildren = in.readInt();
	if (numChildren < 0) {
	    return new DistributionFile(path, size);
	}
	DistributionFile[] children = new DistributionFile[numChildren];
	for (int i = 0; i < numChildren; i++)
	    children[i] = readDf(in);
	return new DistributionFile(path, size, children);
    }
}
