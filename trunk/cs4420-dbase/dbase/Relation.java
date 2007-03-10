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
import java.util.*;

public class Relation {
	
	/**The name of the file which holds this relations records.*/
	private final String filename;
	
	/**The internal, autmatically assigned ID of this relation.*/
	private final int ID;
	
	/**The FileChannel which maps to this relation and reads from it.*/
	private FileChannel channel;
	
	/** The Basic information for the Relation */
	private String relationname;
	private int creationdate;
	private int modifydate;
	private int tuples;
	private ArrayList<Attribute> indexed;
	private ArrayList<String> indexfiles;
	private int blocktotal;
	
	
	
	public int getBlocktotal() {
		return blocktotal;
	}

	public void setBlocktotal(int blocktotal) {
		this.blocktotal = blocktotal;
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
		return indexfiles;
	}

	public void setIndexfiles(ArrayList<String> indexfiles) {
		this.indexfiles = indexfiles;
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

	public int getTuples() {
		return tuples;
	}

	public void setTuples(int tuples) {
		this.tuples = tuples;
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
	
	public void close() {
		//TODO Closes the iterator thingy.
	}
	
	//TODO Finish implementing all other functionality of the class including an attribute map or list, etc.

}
