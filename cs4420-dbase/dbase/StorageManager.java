/*
 * StorageManager.java
 *
 * Created on March 6, 2007, 6:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package dbase;

import java.nio.MappedByteBuffer;

/**
 *
 * @author andrewco
 */
public class StorageManager {
    
    
    /**The System Catalog that owns the buffermanager this belongs to, 
     * used to allow it to get relation files and others.*/
    private SystemCatalog catalog;
    
    
    /** Creates a new instance of StorageManager. */
    public StorageManager() {
        
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
     */
    public boolean openFile(final String fileName) {
        return true;
    }
    
    
    /**This reads the requested block from the requested relation's file
     * and returns it.
     * @param relation the relation to read.
     * @param block the block needed from that relation.
     * @return The MappedByteBuffer of the block specified or null if read 
     * fails.
     */
    public MappedByteBuffer read(final int relation, final long block) {
        return null;
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