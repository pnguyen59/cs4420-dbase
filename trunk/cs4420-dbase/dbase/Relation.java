/**
 * 
 */
package dbase;

/**
 * @author gtg471h
 * 
 * This Class to be used as a wrapper class for all the Relation 
 * information held in the database.  1 instance per Relation.
 */

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class Relation {
	
	/**The name of the file which holds this relations records.*/
	private final String filename;
	
	/**The internal, autmatically assigned ID of this relation.*/
	private final int ID;
	
	/**The FileChannel which maps to this relation and reads from it.*/
	private FileChannel channel;
	
	/** The Basic information for the Relation */
	private String relationname;
	
	/**The date that this relation was created.*/
	private int creationdate;
	
	/**The date that this relation was last modified.*/
	private int modifydate;
	
	/**The number of records in this relation.*/
	private int records;
	
	/**The indexed attributes of this relation.*/
	private ArrayList<Attribute> indexed;
	
	/**The Attributes of this relation.*/
	private ArrayList<Attribute> attributes;
	
	/**The names of the index files of this relation.*/
	private ArrayList<String> indexFiles;
	
	/**The total number of blocks that this relation spans.*/
	private int blockTotal;
	
	/**The size of one record in this relation in bytes.*/
	private int size;
	
	
	public int getBlocktotal() {
		return blockTotal;
	}

	public void setBlocktotal(int blocktotal) {
		this.blockTotal = blocktotal;
	}

	public int getCreationdate() {
		return creationdate;
	}

	public void setCreationdate(int creationdate) {
		this.creationdate = creationdate;
	}

	public ArrayList<Attribute> getIndexed() {
		return indexed;
	}

	public void setIndexed(ArrayList<Attribute> indexed) {
		this.indexed = indexed;
	}

	public ArrayList<String> getIndexfiles() {
		return indexFiles;
	}

	public void setIndexfiles(ArrayList<String> indexfiles) {
		this.indexFiles = indexfiles;
	}

	public int getModifydate() {
		return modifydate;
	}

	public void setModifydate(int modifydate) {
		this.modifydate = modifydate;
	}

	public String getRelationname() {
		return relationname;
	}

	public void setRelationname(String relationname) {
		this.relationname = relationname;
	}

	public int getRecords() {
		return records;
	}

	public void setRecords(int tuples) {
		this.records = tuples;
	}

	/**This creates a new instance of relation.
	 * @param newfilename The file that holds the records of this relation.
	 * @param newID The unique internal ID of this relation.
	 */
	public Relation(final String newfilename, final int newID) {
		this.filename = newfilename;
		this.ID = newID;
		channel = StorageManager.openFile(filename);
		attributes = new ArrayList<Attribute>();
	}

	/**Returns the name of the file that holds the records of this relation.
	 * @return The name of this relations file.
	 */
	public String getFilename() {
		return filename;
	}

	/**Returns the internal ID of this relation.
	 * @return The ID of this relation.
	 */
	public int getID() {
		return ID;
	}

	/**Returns the FileChannel mapped to this relations file.
	 * @return The FileChannel of this relation.
	 */
	public FileChannel getChannel() {
		return channel;
	}
	
	/**This method opens up an Iterator for this relation.
	 * @return An instance of Iterator for this relation.
	 */
	public Iterator open() {
		return new Iterator(this);	
	}
	
	/**This method will return the size of one record in this relation in 
	 * bytes.  This is dynamically caclulated using the size of the attributes
	 * in the relation.
	 * @return The size of one record in this Relation in bytes.
	 */
	public int getSize() {
		//Loop through all of the attributes and get their sizes
		int totalSize = 0;
		for (int attribute = 0; attribute < attributes.size(); attribute++) {
			//Add the size of each attribute to the total size
			totalSize += ((Attribute) attributes.get(attribute)).getSize();
		}
		return totalSize;
	}
	
	/**This method returns the number of records of this relation which can
	 * be placed in one block.
	 * @return The records of this relation which will fit in one block.
	 */
	public int getRecordsPerBlock() {
		return (StorageManager.BLOCK_SIZE / this.getSize());
	}
	
	public void close() {
		//TODO Closes the iterator thingy.
	}
	
	/**This method calculates whether or not the last block of the relation is
	 * full and returns the result.  It looks to see if the total number of
	 * blocks * the records per block is equal to the total number of records.
	 * @return Whether or not the last block is full.
	 */
	public boolean isLastBlockFull() {
		return ((blockTotal * this.getRecordsPerBlock()) == records);
	}
	
	/**This method will return where in the last block a new record should
	 * start.
	 * @return Where a new record should go in the last block.
	 */
	public int getLastRecordStart() {
		return (records % this.getRecordsPerBlock()) * this.getSize();
	}
	
	public boolean addRecord (ByteBuffer block, String record) {
    	//Parse the record to be inserted into its single attributes
    	String [] attributeValues = record.split("/\\s/");
    	
    	//GEt the start of the record
    	int start = this.getLastRecordStart();
    	
    	//Go through the attributes add add the things.
    	for (int attribute = 0; attribute < attributes.size(); attribute++) {
    		
    		//Get the current attribute from the list of attributes.
    		Attribute currentAttribute = (Attribute) attributes.get(attribute);
    		
    		//Find out what kind it is, write it to the block.
    		if (currentAttribute.getType() == Attribute.Type.Int) {
    			block.putInt(start, Integer.parseInt(
    				attributeValues[attribute]));
    		} else if (currentAttribute.getType() == Attribute.Type.Char) {
    			writeString(block, attributeValues[attribute], start,
    					currentAttribute.getSize());
    		} else if (currentAttribute.getType() == Attribute.Type.Long) {
    			block.putLong(start, Long.parseLong(
    				attributeValues[attribute]));
    		} else if (currentAttribute.getType() == Attribute.Type.Float) {
    			block.putFloat(start, Float.parseFloat(
    				attributeValues[attribute]));
    		} else if (currentAttribute.getType() == Attribute.Type.Double) {
    			block.putDouble(start, Double.parseDouble(
        			attributeValues[attribute]));
        	}
    		
    		//Then for the next attribute, move past this one's size.
    		start += currentAttribute.getSize();
    	}
    	
    	//Return that the record was added to the relation.
		return true;
	}
	
	/**
	 * This method is responsible for adding an attribute to the method
	 * @param name what we're calling the attribute
	 * @param type the type of the attribute
	 * @param ID the internal ID
	 * @return true if successful
	 */
	public Attribute addAttribute(String name, Attribute.Type type, int ID){
		Attribute att = new Attribute(name, type, ID);
		attributes.add(att);
		return att;
	}
	
	public String toString(){
		return "Relation "+ID+" named: "+this.filename+" with "+attributes.size()
			+" attributes: "+attributes.toString()+"\n";
	}
	
	 /**This method will insert the specified record into the specified
     * relation.
     * @param relation The relation in which to insert the record.
     * @param record The record to be inserted.
     * @return Whether or not the insertion succeeded.
     */
    public boolean insert(final int relationID, final String record) {

    	//TODO First see if a record exists with this key.  If so then return
    	//false or print an error or some shit.  Either way don't inser it.
    	
    	//The last block is blockTotal - 1 cause the first block is 0.  This
    	//is where we are going to write to.
    	long lastBlock = blockTotal - 1;
    	
    	//Get the BufferManager
    	BufferManager buffer = BufferManager.getBufferManager();
    	
    	//See if there is enough space in the last block for another record.
    	if (isLastBlockFull()) {
    		//If there isn't, generate a new block and write it to the file
    		//of this relation.
    		long blockAddress = BufferManager.makePhysicalAddress(relationID
    				, lastBlock + 1);
    		buffer.writePhysical(blockAddress, 
    				BufferManager.getEmptyBlock());
    		//Then increment the block count of the relation
    		setBlocktotal(getBlocktotal() + 1);
    	}

    	//Then regardless, the last block of the relation has enough space in
    	//it, so have the last block loaded into the buffer.
    	ByteBuffer block = buffer.read(
    			relationID, getBlocktotal() - 1);
    	
    	//Then, when we know that we have a block that has space in it, write
    	//this new record to the block
    	addRecord(block, record);

    	return true;
    }
	
    /**This method will add an index file to the list of files maintained by
     * the Relation.
     * @param fileName The name of the index file to add.
     * @return Whether or not the index file was successfully added.
     */
	public boolean addIndex(final String fileName) {
		//If the index doesn't already exist then add it.
		if (!containsIndex(fileName)) {
			indexFiles.add(fileName);
			return true;
		}
		//If it already exists, then don't add it and return false.
		return false;
	}
	
	/**Checks whether or not this relation contains the specified index file.
	 * @param fileName The index we are checking.
	 * @return Whether or not this relation contains the specified index.
	 */
	public boolean containsIndex(final String fileName) {
		//Looop through the list of indexes and see if it contains the file
		for (int index = 0; index < indexFiles.size(); index++) {
			if (((String) indexFiles.get(index)).equalsIgnoreCase(fileName)) {
				return true;
			}
		}
		//If it doesn't then return false.
		return false;
	}
	
	/**This method will write the given string to the given block a the given
	 * position, and will fill in the rest of the space for this attribute
	 * with '\0'.  
	 * @param block The block to write these to.
	 * @param chars The string of characters to write.
	 * @param start Where this CHAR should be written in the block.
	 * @param size This size of this CHAR() attribute, in bytes.
	 * @return
	 */
	private boolean writeString(final ByteBuffer block, 
		final String chars, int start, final int size) {
		
		//Loop through the String, writing the characters as ints.
		for (int index = 0; index < chars.length();  index++) {
			//Get the char at the index character
			char character = chars.charAt(index);
			block.putInt(start, (int) character);
			//Then add the size of an int to start
			start += 2;
		}
		//After all of the members of the string have been written, fill
		//in the rest of the space with '\O'
		for (int nulls = 0; nulls < (size - chars.length()); nulls++) {
			block.putInt(start, (int) BufferManager.NULL_CHARACTER);
			start += 2;
		}
		
		//Then return true;
		return true;
	}
	
}
