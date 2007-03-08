/*
 * StorageManager.java
 *
 * Created on March 6, 2007, 6:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package dbase;

import java.util.*;
import java.nio.*;

/**
 *
 * @author andrewco
 */
public class StorageManager {
    
    
    /**
     *The System Catalog that owns the buffermanager this belongs to, used to allow it to get relation files and others.
     *
     **/
    private SystemCatalog catalog;
    
    
    /** Creates a new instance of StorageManager */
    public StorageManager() {
        
    }
    
    /**
     *This scans an index and returns the blocks.
     *
     *@param index The Index ID to load.
     *
     *@return the first block of the index.
     **/
    public MappedByteBuffer indexScan(int index) {
        return null;
    }
    
    /**
     *This creates a new file for whatever the hell you want.
     *
     *@param file the filename
     *
     *@return whether or not the file was sucessfully created.
     */
    public boolean makeFile(String file) {
        return true;
    }
    
    /**
     *This opens a file for the storage manager to read into or write out of.
     *
     *@param file the filename to open
     *
     *@return the opened file stream (implement)
     */
    public boolean openFile(String file) {
        return true;
    }
    
    
    /**
     *This reads the requested block from the requested relation's block and returns it.
     *
     *@param relation the relation to read.
     *@param block the block needed from that relation.
     *
     *@return the MappedByteBuffer of the block specified or null if read fails.
     */
    public MappedByteBuffer read (int relation, long block) {
        return null;
    }
    
    /**
     *Reads in the first block of the table.
     *
     *@param table the ID of the relation to scan.
     *
     *@return the first block.
     */
    
    public MappedByteBuffer tableScan(int table) {
        return null;
    }
    
    /**
     *This writes the requested block to the requested relation's block and writes it.
     *
     *@param relation the relation to read.
     *@param address the block needed from that relation.
     *@param block the information to be written
     *
     *@return whether or not the write succeeded.
     */
    
    public boolean write(int relation, long address, MappedByteBuffer block) {
        return true;
    }
}
