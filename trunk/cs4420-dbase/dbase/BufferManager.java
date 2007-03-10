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
import java.nio.*;

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
    
    /**Writes all non-pinned blocks to disk to clear the buffer.
     *@return Whether ot not the fluxh succeeded.
     */
    public boolean flush() {
        return true;
    }
    
    
    /**Finds a spot to free up for a new block in memory and return it's
     *logical address.
     *@return The logical address of the freed block.
     */
    private long freeSpace() {
    	//go till we find one
	    while (true) {
	    	//if it is 0 return it
			if (lookUpTable[(int) time][1] == 0) {
				return time;
			} else {
				//if it's pinned just move on
				if (lookUpTable[(int) time][1] == -1) {
					time++;
				} else {
					//if it's not pinned decrement and move on
					lookUpTable[(int) time][1]--;
					time++;
				}
			}
		}
    }
    
    
    /**Pins a block currently in memory.
     *@param relation The relation containing the block to be pinned.
     *@param block  The block in the relation to be pinned.
     *@return Whether or not the block was successfully pinned.
     */    
    public boolean pin(final int relation, final long block) {
//    	make physical address
    	long address = block * 100 + relation;
    	//look for that address
    	for (int i = 0; i < lookUpTable.length; i++) {
    		if (address == lookUpTable[i][0]) {
    			//see if it is pinned
    			if (lookUpTable[i][1] != (long)-1) {
    				//found and unpinned
    				lookUpTable[i][1] = -1;
    				return true;
    			}
    			//found but not pinned
    			return false;
    		}
    	}
    	//not found
    	return false;
    }
    
    /**Reads a block from memory, will load block into memory if not already in
     *the buffer.
     *@param relation The relation that the requested block is in.
     *@param address The block from the specified relation.
     *@return The block requested in a MappedByteBuffer.
     */
    public MappedByteBuffer read(final int relation, final long address) {
    	//TODO fix this obviously.
    	freeSpace();
        return null;
    }
    
    /**Removes the pin from a block in memory.
     *@param relation The ID of the relation containing the block to be 
     *unpinned.
     *@param block The block in the relation to be unpinned.
     *@return Whether or not the block was successfully unpinned.
     */
    public boolean unpin(final int relation, final long block) {
    	//make physical address
    	long address = block * 100 + relation;
    	//look for that address
    	for (int i = 0; i < lookUpTable.length; i++) {
    		if (address == lookUpTable[i][0]) {
    			//see if it is pinned
    			if (lookUpTable[i][1] == (long)-1) {
    				//found and pinned
    				lookUpTable[i][1] = 1;
    				return true;
    			}
    			//found but not pinned
    			return false;
    		}
    	}
    	//not found
    	return false;
    }
    
    /***Writes the specified block to disk. given the physical address
     *@param address The <b>logical</b> address to be written to disk.
     *@param data the ByteBuffer of data to be written to the MappedByteBuffer in buffer.
     *@return whether the block was successfully written.
     */
    public boolean writePhysical(final long address, ByteBuffer data) {
    	int logical;
    	for (int i = 0; i < buffer.length; i++) {
    		if (lookUpTable[i][0] == address) {
    			logical = i;
    			buffer[logical].put(data);
    	    	return true;
    		}
    	}
    	return false;
    }
    
    /***Writes the specified block to disk. given the logical address
     *@param address The <b>logical</b> address to be written to disk.
     *@param data the ByteBuffer of data to be written to the MappedByteBuffer in buffer.
     *@return whether the block was successfully written.
     */
    
    public boolean writeLogical(final long address, ByteBuffer data) {
    	buffer[(int)address].put(data);
    	return true;
    }
    
    
    /**This method will return whether or not the specified block is pinned in
     * memory. Please note all Physical Addresses are in the format: &&####%%
     * where #### is the block number, %% is the relation ID, and && is the record number on the block
     * @param relation The relation containing the block.
     * @param block The block inside of that relation.
     * @return Whether or not the block is pinned.
     */
    public boolean isPinned(final int relation, final long block) {
    	//make physical address
    	long address = block * 100 + relation;
    	//look for that address
    	for (int i = 0; i < lookUpTable.length; i++) {
    		if (address == lookUpTable[i][0]) {
    			//see if it is pinned
    			if (lookUpTable[i][1] == (long)-1) {
    				//found and pinned
    				return true;
    			}
    			//found but not pinned
    			return false;
    		}
    	}
    	//not found
    	return false;
    }
}
