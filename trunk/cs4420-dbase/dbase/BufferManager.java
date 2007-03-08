/*
 * BufferManager.java
 *
 * Created on March 6, 2007, 5:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dbase;

import java.nio.MappedByteBuffer;

/**
 * @author andrewco
 */
public class BufferManager {
    
    /**The Buffer Implenetation.*/
    private MappedByteBuffer [] buffer;
    
    /**A lookup table for the buffer.*/
    private long [][] lookUpTable;
    
    /**The current position of the clock.*/
    private long time = 0;
    
    /**Creates a new instance of BufferManager.*/
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
    
    
    /**Finds a spot to free up for a new block in memory and return it's
     *logical address.
     *@return The logical address of the freed block.
     */
    private long freeSpace() {
        return time; 
    }
    
    
    /**Pins a block currently in memory.
     *@param relation The relation containing the block to be pinned.
     *@param block  The block in the relation to be pinned.
     *@return Whether or not the block was successfully pinned.
     */    
    public boolean pin(final int relation, final long block) {
        return true;
    }
    
    /**Reads a block from memory, will load block into memory if not already in
     *the buffer.
     *@param relation The relation that the requested block is in.
     *@param address The block from the specified relation.
     *@return The block requested in a MappedByteBuffer.
     */
    public MappedByteBuffer read(final int relation, final long address) {
        return null;
    }
    
    /**Removes the pin from a block in memory.
     *@param relation The ID of the relation containing the block to be 
     *unpinned.
     *@param block The block in the relation to be unpinned.
     *@return Whether or not the block was successfully unpinned.
     */
    public boolean unpin(final int relation, final long block) {
        return true;
    }
    
    /***Writes the specified block to disk.
     *@param address The <b>logical</b> address to be written to disk.
     *@return whether the block was successfully written.
     */
    public boolean write(final long address) {
        return true;
    }
    
    /**This method will return whether or not the specified block is pinned in
     * memory.
     * @param relation The relation containing the block.
     * @param block The block inside of that relation.
     * @return Whether or not the block is pinned.
     */
    public boolean isPinned(final int relation, final long block) {
    	return false;
    }
}
