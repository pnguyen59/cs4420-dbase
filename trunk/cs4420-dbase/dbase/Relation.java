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
	
}
