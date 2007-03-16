package dbase;

import java.nio.MappedByteBuffer;

/**
 * 
 * @author andrewco
 *
 */
public class Iterator {

	/**The Relation that the Iterator will operate on.*/
	private Relation relation;
	
	/**The block that this iterator is currently working on.*/
	private long currentBlock;
	
	/**The next record that this iterator will return from its current block.*/
	private int nextRecord;
	
	public Iterator() {
		
	}
	
	/**Creates a new instance of Iterator that will work on the specified
	 * relation.
	 * @param newRelation The relation who's records the new Iterator will 
	 * fetch.
	 */
	public Iterator(final Relation newRelation) {
		this.relation = newRelation;
		System.out.println(relation); //TODO delete later, used to remove warning about not using relation.
	}
	
	/**Thie method will close the iterator.
	 * @return Whether or not the close was successful.
	 */
	public boolean close() {
		return true;
	}
	
	/**This method will return the next record in the relation that it is 
	 * operating on.
	 * @return The next record in the relation as a string.
	 */
	public String getNext() {
		
		//Get the block that were using from the buffer manager.
		MappedByteBuffer block = null;
		//See if the next record that were are supposed to get is outside of
		//this block
		//The relations per block is block size / relation size
		int recordsPerBlock = StorageManager.BLOCK_SIZE / relation.getSize();
		if (nextRecord > recordsPerBlock * currentBlock) {
			//TODO do whatever is to be done when the next record is not in the
			//current block.
		}
		
		//If it isn't then find the record's bytes within this block
		byte [] bytes = block.array();
		//Find out how many records there are in this block
		long blockRecords = relation.getRecords() 
			- ((long) (currentBlock) * (long) recordsPerBlock);
		long previousRecords = relation.getRecords() - blockRecords;
		
		//TODO Have the relation parse out the record into whatever it's supposed to
		//be
		
		//TODO Return the record as a string
		
		return null;
	}
	
	/** DEPRECATED - ParseRecord is a utility method for all classes to use.  It is meant
	 * to be able to parse a record from a given block if you tell it which 
	 * relation the block is from.
	 * @param block The block that the record is in.
	 * @param relation The relation that this block is from.
	 * @param record Which record in the block to fetch.
	 * @return The requested record as a String;
	 */
//	public static String parseRecord(final MappedByteBuffer block, 
//			final int relation,
//			final int record) {
//		//TODO Is this still needed?
//		return null;
//	}
	
}
