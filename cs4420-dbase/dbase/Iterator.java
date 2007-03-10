package dbase;

import java.nio.MappedByteBuffer;

/**
 * 
 * @author andrewco
 *
 */
public class Iterator {

	public Iterator() {
		
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
		return null;
	}
	
	/**ParseRecord is a utility method for all classes to use.  It is meant
	 * to be able to parse a record from a given block if you tell it which 
	 * relation the block is from.
	 * @param block The block that the record is in.
	 * @param relation The relation that this block is from.
	 * @param record Which record in the block to fetch.
	 * @return The requested record as a String;
	 */
	public static String parseRecord(final MappedByteBuffer block
			, final int relation,
			final int record) {
		return null;
	}
	
}
