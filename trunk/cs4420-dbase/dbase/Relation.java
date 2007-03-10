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

import java.nio.channels.*;

public class Relation {
	
	private final String filename;
	private final int ID;
	private FileChannel channel;
	
	public Relation(String filename, int ID) {
		this.filename = filename;
		this.ID = ID;
		channel = StorageManager.openFile(filename);
	}

	public String getFilename() {
		return filename;
	}

	public int getID() {
		return ID;
	}

	public FileChannel getChannel() {
		return channel;
	}
	
	//TODO Finish implementing all other functionality of the class including an attribute map or list, etc.

}
