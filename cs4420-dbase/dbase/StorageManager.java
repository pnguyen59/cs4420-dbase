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
import java.io.*;

/**
 *
 * @author andrewco
 */
public class StorageManager {
    
    
    /**The System Catalog that owns the buffermanager this belongs to, 
     * used to allow it to get relation files and others. 
     * Replaced with static methods in Storage Manager*/
    //private SystemCatalog catalog;
	
	/**
	 * The RelationHolder Class to hold relation lists for filename access etc.
	 * Grabs the Relation list from RelationHolder.
	 */
	
	private RelationHolder relationholder = RelationHolder.getRelationHolder();
    
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
//    public StorageManager(final SystemCatalog newCatalog) {
//    	this.catalog = newCatalog;
//    }
    
    /**This scans an index and returns the first block from the index.
     * @param relation The relation for which the index is being found.
     * @param index The Index ID to load.
     * @return The first block of the index as a MappedByteBuffer.
     */
    public MappedByteBuffer indexScan(final int relation, final int index) {
        return null;
    }
    
    /**This creates a new file wiht the given name.
     * --Moved into code in openFile--
     * @param fileName The name of the new file to be created.
     * @return Whether or not the file was sucessfully created.
     */
//    public boolean makeFile(final String fileName) {
//    	//TODO make the file for this relation.
//    	
//    	
//    	
//        return true;
//    }
    
    /**This opens a file for the storage manager to read into or write out of.
     * @param fileName The name of the file to open.
     * @return The FileChannel mapped to the newly opened file, or null if it
     * could not be opened.
     */
    private FileChannel openFile(final String fileName) { 	
    	RandomAccessFile file;
		FileChannel channel = null;
    	try {
    		//Try to open the RandomAccessFile pointing to fileName and 
    		//get the channel from it.
    		file = new RandomAccessFile(fileName, "rw");
    		channel = file.getChannel();
    	} catch (FileNotFoundException exception) {
    		//If open failed because file doesn't exist, create file and open it.
    		try {
    			File newfile = new File(fileName);
    			boolean worked = newfile.createNewFile();
    			if (worked) {
    				System.out.println("File Successfully Created.");
    				return openFile(fileName);
    			} else {
    				System.out.println("File Creation Unsuccesful.");
    			}
    		
    		} catch(IOException e) {
    			System.out.println("Couldn't create file " + fileName);
    			System.out.println("Exception " + e.toString());
    			System.exit(1);
    		}
    		//If the file can't be found then print that, the error and exit.
    		System.out.println("Couldn't open file " + fileName 
    				+ " in StorageManager.openFile");
    		System.out.println("Exception" + exception.toString());
    		System.exit(1);
    	}
        return channel;
    }
    
    
    /**This reads the requested block from the requested relation's file
     * and returns it.
     * @param relation The relation to read the block from.
     * @param block The block needed from that relation.
     * @return The MappedByteBuffer of the block specified or null if read 
     * fails.
     */
    public MappedByteBuffer read(final int relation, final long block) {   	
    	
    	MappedByteBuffer buffer = null;
		FileChannel channel = null;
		
    	//TODO Get the filename for the relation passed to this method from the
		//catalog
		String file = "/Documents/test.txt";
    	
		//Get the FileChannel for the specified relation
		channel = this.openFile(file);
		
		//TODO have it see if the requested block is beyond the end of the
		//file, or just greater than the file size.  Just some way it can't
		//be read
		int size = isBlockInRange(channel, block);
		
		//In this try/catch block, we try to read in the specified block from
		//the file
		try {
			//TODO When ready for the real shit, take this off of
			//READ_ONLY
			buffer = channel.map(
					FileChannel.MapMode.READ_ONLY, 
					block * BLOCK_SIZE, size);
		} catch (IOException e) {
			System.out.println("Couldn't map bytes from file " + file);
			System.exit(1);
		}
		
        return buffer;
    }
    
    /**Reads in the first block of the specified relation.
     * @param relation The ID of the relation to scan.
     * @return The first block of the relation as a MappedByteBuffer.
     */
    public MappedByteBuffer tableScan(final int relation) {
    	//TODO have it get the first block of the table
        return null;
    }
    
    /**This method is meant to detect reads or writes which are out of the range
     * of the specified file.
     * @param file The file for which the range will be checked.
     * @param block The block we want to write.
     * @return The size of the block that can be grabbed
     */
    private int isBlockInRange(final FileChannel file, final long block) {
    	
    	//Default to the standard block size
    	int returnSize = BLOCK_SIZE;
    	
    	//Get the size of the file in bytes
    	long fileSize = 0;
    	try {
    		fileSize = file.size();
    	} catch (IOException exception) {
    		System.out.println("The size of the file couldn't be determined"
    				+ "in StorageManager.isBlockInRange");
    		System.out.println(exception);
    		System.exit(1);
    	}
    	
    	//Now see if the block requested is longer than the range of the file
    	long blockByte = (block + 1) * BLOCK_SIZE;
    	//If the fileSize is smaller than the requested block then we have a
    	//problem
    	if (fileSize < blockByte) {
        	//First see if the last block in the file isn't full.  If it is then
        	//return however many bytes the last block has in it.
    		if ((fileSize % BLOCK_SIZE) != 0) {
    			returnSize = (int) (fileSize % BLOCK_SIZE);
    		} else {
    			//If that isn't it then return -1 cause the requested block 
    			//is definately not in the file.
    			returnSize = -1;
    		}
    	}	
    	return returnSize;
    }
    
    /**This writes the given block to the block in the relation specified.
     * @param relation The relation to write the block to.
     * @param address The block to write to.
     * @param block The information to be written to disk.
     * @return Whether or not the write succeeded.
     */  
    public boolean write(final int relation, final long address, 
    		final MappedByteBuffer block) {
    	//TODO figure out how to write
    	
    	//TODO have it go from the relation ID to the file name.
        return true;
    }
}
