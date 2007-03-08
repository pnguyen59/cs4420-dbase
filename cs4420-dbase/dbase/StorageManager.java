/*
 * StorageManager.java
 *
 * Created on March 6, 2007, 6:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package dbase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author andrewco
 */
public class StorageManager {
    
    
    /**The System Catalog that owns the buffermanager this belongs to, 
     * used to allow it to get relation files and others.*/
    private SystemCatalog catalog;
    
    /**This is the size of the block in bytes which will be taken into 
     * and returned from this class.
     */
    public static final int BLOCK_SIZE = 4096;
    
    /** Creates a new instance of StorageManager with nothing inside
     * of it.
     */
    public StorageManager() {
        
    }
    
    /**Creates a new instance of StorageManager with the specified 
     * SystemCatalog.
     * @param newCatalog The SystemCatalog which this StorageManager will refer
     * to for information regarding file names and relation names.
     */
    public StorageManager(final SystemCatalog newCatalog) {
    	this.catalog = newCatalog;
    }
    
    /**This scans an index and returns the first block from the index.
     * @param relation The relation for which the index is being found.
     * @param index The Index ID to load.
     * @return The first block of the index as a MappedByteBuffer.
     */
    public MappedByteBuffer indexScan(final int relation, final int index) {
        return null;
    }
    
    /**This creates a new file wiht the given name.
     * @param fileName The name of the new file to be created.
     * @return Whether or not the file was sucessfully created.
     */
    public boolean makeFile(final String fileName) {
        return true;
    }
    
    /**This opens a file for the storage manager to read into or write out of.
     * @param fileName The name of the file to open.
     * @return The FileChannel mapped to the newly opened file, or null if it
     * could not be opened.
     * @throws FileNotFoundException Throws if the file specified cannot be
     * found.
     */
    private FileChannel openFile(final String fileName) 
    	throws FileNotFoundException {
    	
    	RandomAccessFile file;
    	FileChannel channel = null;
    	
		//Try to open the RandomAccessFile pointing to fileName and 
		//get the channel from it.
		file = new RandomAccessFile("/Documents/test.txt",
				"r");
		channel = file.getChannel();
		
        return channel;
    }
    
    
    /**This reads the requested block from the requested relation's file
     * and returns it.
     * @param relation the relation to read.
     * @param block the block needed from that relation.
     * @return The MappedByteBuffer of the block specified or null if read 
     * fails.
     */
    public MappedByteBuffer read(final int relation, final long block) {   	
    	//TODO Get the filename for the relation passed to this method
    	
		MappedByteBuffer buffer = null;
		FileChannel channel = null;
		try {
			//TODO Replace this dummy file.
			channel = this.openFile("/Documents/test.txt");
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't open file test.txt");
			System.out.println(e);
		}
		
		//TODO have it see if the requested segment is beyond the end of the
		//file, or just greater than the file size
		//In this try/catch block, we try to read in the specified block from
		//the file
		try {
			//Grab the block with the specified size and offset
			buffer = channel.map(
					FileChannel.MapMode.READ_ONLY, 
					block * BLOCK_SIZE, BLOCK_SIZE);
		} catch (IOException e) {
			//TODO Replace dummy file test.txt
			System.out.println("Couldn't map bytes from file test.txt");
			System.out.println(e);
		}
		
        return buffer;
    }
    
    /**Reads in the first block of the specified relation.
     * @param relation The ID of the relation to scan.
     * @return The first block of the relation as a MappedByteBuffer.
     */
    public MappedByteBuffer tableScan(final int relation) {
        return null;
    }
    
    /**This writes the given block to the block in the relation specified.
     *@param relation The relation to write the block to.
     *@param address The block to write to.
     *@param block The information to be written to disk.
     *@return Whether or not the write succeeded.
     */  
    public boolean write(final int relation, final long address, 
    		final MappedByteBuffer block) {
        return true;
    }
}
