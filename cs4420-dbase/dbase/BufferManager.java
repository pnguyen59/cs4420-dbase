/*
 * BufferManager.java
 *
 * Created on March 6, 2007, 5:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dbase;

import java.nio.*;
/**
 *
 * @author andrewco
 */
public class BufferManager {
    
    /** The Buffer Implenetation. */
    private MappedByteBuffer [] buffer;
    
    /** A lookup table for the buffer */
    private long [][] lookUpTable;
    
    /** The current position of the clock */
    private long time = 0;
    
    /** Creates a new instance of BufferManager */
    public BufferManager() {
    }
    
    /**
     *Flushes All non-pinned blocks to disk to clear the buffer.
     *
     *@return whether ot not the fluxh succeeded.
     */
    public boolean flush() {
        return true;
    }
    
    
    /**
     *Finds a spot to free up for a new block in memory and returns it's location.
     *
     *@return The freed buffer block.
     */
    private long freeSpace() {
        return time; 
    }
    
    
    /**
     *Pins a block into memory.
     *
     *@param address the physical address of the block to pin into memory
     *
     *@return whether the block was successfully pinned.
     */
            
    public boolean pin(long address) {
        return true;
    }
    
    /**
     *Reads a block from memory, will load block into memory if not present.
     *
     *@param address The physical address of the block wanted
     *
     *@return the block requested.
     */
    public MappedByteBuffer read(long address) {
        return null;
    }
    
    /**
     *Removes the pin from a block in memory.
     *
     *@param address The physical address of the block to be unpinned.
     *
     *@return whether the block was successfully unpinned.
     */
    public boolean unpin(long address) {
        return true;
    }
    
    /**
     *Writes a particular block to disk.
     *
     *@param address The <b>logical</b> address to be written to disk.
     *
     *@return whether the block was successfully written.
     */
    public boolean write(long address) {
        return true;
    }
}
