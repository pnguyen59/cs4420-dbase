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
import java.util.Date;

public class Relation {
	
	/**This method will parse a string out of a record, if you tell it where
	 * it starts and how long the string is.
	 * @param record The record containing the string.
	 * @param start Where the string starts in the record, in bytes.
	 * @param size  The size of the string in bytes.
	 * @return The string parsed out of this record.
	 */
	public static String parseString(final ByteBuffer record, final int start, 
			final int size) {
		//System.out.println("Parsing string starting at: " + start 
		//		+ " and of size " + size);
		
		//Loop through the record, getting thigs out as integers and casting
		//them as characters.
		String total = "";
		int offset = start;
		
		for (int current = 0;  current < size; current++) {
			//Get the next character from the record and add it to the total
			char next = record.getChar(offset);
			
			//If it is a null character, then stop her and return what we have.
			if (next == '\0') {
				return total;
			}
			
			total = total + next;
			
			//Increment the start so it gets the next character
			offset += Attribute.CHAR_SIZE;
		}
		return total;	
	}
	
	/**The Attributes of this relation.*/
	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	
	/**The total number of blocks that this relation spans.*/
	private int blockTotal;
	
	/**The FileChannel which maps to this relation and reads from it.*/
	private FileChannel channel;
	
	/**The date that this relation was created.*/
	private long creationdate;
	
	/**The name of the file which holds this relations records.*/
	private final String filename;
	
	/**The internal, autmatically assigned ID of this relation.*/
	private final int ID;
	
	/**The indexed attributes of this relation.*/
	private ArrayList<Integer> indexed = new ArrayList<Integer>(10);
	
	/**The names of the index files of this relation.*/
	private ArrayList<String> indexFiles;;
	
	/**The date that this relation was last modified.*/
	private long modifydate;
	
	/**The number of records in this relation.*/
	private int records;
	
	/** The Basic information for the Relation */
	private String relationname;
	
	
	/**The size of one record in this relation in bytes.*/
	private int size;

	/**This creates a new instance of relation.
	 * @param newfilename The file that holds the records of this relation.
	 * @param newID The unique internal ID of this relation.
	 */
	public Relation(final String relationname, final int newID) {
		this.filename = relationname + ".dbd";
		this.relationname = relationname;
		this.ID = newID;
		channel = StorageManager.openFile(filename);
		attributes = new ArrayList<Attribute>();
		creationdate = (new Date()).getTime();
		modifydate = (new Date()).getTime();
		blockTotal = 1;
		indexFiles = new ArrayList<String>();
	}
	
	public void addAttribute(Attribute att){
		attributes.add(att);
		modifydate = (new Date()).getTime();
	}
	
	/**This method is responsible for adding an attribute to the method
	 * @param name what we're calling the attribute
	 * @param type the type of the attribute
	 * @param newID the internal ID
	 * @return true if successful
	 */
	public Attribute addAttribute(String name, Attribute.Type type, int newID) {
		//Determine the ID of this attribute
		Attribute att = new Attribute(name, type, newID);
		att.setParent(ID);
		attributes.add(att);
		modifydate = (new Date()).getTime();
		return att;
	}

	/**This method is responsible for adding a CHAR attribute to this
	 * relation.
	 * @param name What we're calling the attribute
	 * @param type The Type of this new attribute.
	 * @param newID The internal ID of the new attribute.
	 * @param length The length of the new CHAR attribute.
	 * @return true if successful
	 */
	public Attribute addAttribute(final String name, final Attribute.Type type, 
			final int newID, final int length) {
		//Determine the ID of this attribute
		Attribute att = new Attribute(name, type, newID, length);
		att.setParent(ID);
		attributes.add(att);
		modifydate = (new Date()).getTime();
		return att;
	}

	public void addIndex(int j){
		indexed.add(new Integer(j));
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
			modifydate = (new Date()).getTime();
			return true;
		}
		//If it already exists, then don't add it and return false.
		return false;
	}
	
	public boolean addRecord (ByteBuffer block, String record) {
    	//Parse the record to be inserted into its single attributes
    	String [] attributeValues =
    		record.substring(record.indexOf("(")+1,record.indexOf(")")).split(",");
    	
    	//GEt the start of the record
    	int start = this.getLastRecordStart();
  
    	//Go through the attributes add add the things.
    	for (int attribute = 0; attribute < attributes.size(); attribute++) {
    		
    		//Get the current attribute from the list of attributes.
    		Attribute currentAttribute = (Attribute) attributes.get(attribute);
    		
    		//Find out what kind it is, write it to the block.
    		if (currentAttribute.getType() == Attribute.Type.Int) {
    			block.putInt(start, Integer.parseInt(
    				attributeValues[attribute].trim()));
    		} else if (currentAttribute.getType() == Attribute.Type.Char) {
    			writeString(block, attributeValues[attribute].trim(), start,
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
    		
    		currentAttribute.addValue(attributeValues[attribute].trim());
    		  		
    		//Then for the next attribute, move past this one's size.
    		start += currentAttribute.getSize();
    		modifydate = (new Date()).getTime();
    	}
    	
    	//System.out.println("Added a record to relation " + relationname
    	//		+ " Block now contains " + block.asCharBuffer() + "...");
    	this.records++;
    	//Return that the record was added to the relation.
		return true;
	}

	public boolean addRecord(final ByteBuffer block, final String record, 
		final String attribute) {
		
    	//Parse the record to be inserted into its single attributes
    	String [] attributeValues = record.substring(
    		record.indexOf("(")+1,record.indexOf(")")).split(",");
    	String [] attributeNames = attribute.substring(
    		attribute.indexOf("(")+1,attribute.indexOf(")")).split(",");
    	//GEt the start of the record
    	int offset = this.getLastRecordStart();
    	
    	//Go through the attributes add add the things.
    	for (int j = 0; j < attributeNames.length; j++) {
    			
    		//Get the current attribute from the list of attributes.
    		Attribute currentAttribute = 
    			getAttributeByName(attributeNames[j].trim());
    		
    		int start = offset + getAttributeBlockPosition(currentAttribute);
    		
    		//System.out.println("Adding attribute " + attributeNames[j] 
    		//	+ " a byte " + start  + "...");
    		
    		//Find out what kind it is, write it to the block.
    		if (currentAttribute.getType() == Attribute.Type.Int) {
    			block.putInt(start, Integer.parseInt(
    				attributeValues[j].trim()));
    		} else if (currentAttribute.getType() == Attribute.Type.Char) {
    			writeString(block, attributeValues[j].trim(), start,
    					currentAttribute.getSize());
    		} else if (currentAttribute.getType() == Attribute.Type.Long) {
    			block.putLong(start, Long.parseLong(
    				attributeValues[j]));
    		} else if (currentAttribute.getType() == Attribute.Type.Float) {
    			block.putFloat(start, Float.parseFloat(
    				attributeValues[j]));
    		} else if (currentAttribute.getType() == Attribute.Type.Double) {
    			block.putDouble(start, Double.parseDouble(
        			attributeValues[j]));
        	}
    		
    		currentAttribute.addValue(attributeValues[j].trim());
    		
    		//Then for the next attribute, move past this one's size.
    		start += currentAttribute.getSize();
    		modifydate = (new Date()).getTime();
    	}
    	
    	//System.out.println("Added a record to relation " + relationname
    	//		+ " Block now contains " + block.asCharBuffer() + "...");
    	this.records++;
    	//Return that the record was added to the relation.
		return true;
	}
	
	public void close() {
		//TODO Closes the iterator thingy.
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

	public int getAttributeBlockPosition(Attribute att){
		if (attributes.indexOf(att) == -1) return -1;
		else {
			int size = 0;
			for (int j = 0; j < attributes.indexOf(att); j++){
				size += attributes.get(j).getSize();
			}
			return size;
		}
	}

	public Attribute getAttributeByName(String name){
		for (int j=0; j< attributes.size(); j++){
			if (attributes.get(j).getName().equals(name)){
				return attributes.get(j);
			}
		}
		return null;
	}

	public int getBlocktotal() {
		return blockTotal;
	}

	public int getBlockTotal() {
		return blockTotal;
	}

	/**Returns the FileChannel mapped to this relations file.
	 * @return The FileChannel of this relation.
	 */
	public FileChannel getChannel() {
		return channel;
	}
	
	public long getCreationdate() {
		return creationdate;
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

	public int getIndexByName(final String name) {
		for (int index = 0; index < attributes.size(); index++) {
			Attribute current = (Attribute) attributes.get(index);
			//System.out.println("Comparing " + name +
				//	" and " + current.getName());
			if (name.equalsIgnoreCase(current.getName())) {
				return index;
			}
		}
		return -1;
	}

	public ArrayList<Integer> getIndexed() {
		return indexed;
	}

	public ArrayList<String> getIndexfiles() {
		return indexFiles;
	}

	/**This method will return where in the last block a new record should
	 * start.
	 * @return Where a new record should go in the last block.
	 */
	public int getLastRecordStart() {
		return (records % this.getRecordsPerBlock()) * this.getSize();
	}

	public long getModifydate() {
		return modifydate;
	}

	public String getName(){
		return relationname;
	}
	
	public int getRecords() {
		return records;
	}
	
	
	
	/**This method returns the number of records of this relation which can
	 * be placed in one block.
	 * @return The records of this relation which will fit in one block.
	 */
	public int getRecordsPerBlock() {
		return (StorageManager.BLOCK_SIZE / this.getSize());
	}
	
	public String getRelationname() {
		return relationname;
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
			//System.out.println("Read an attribute");
			totalSize += ((Attribute) attributes.get(attribute)).getSize();
		}
		return totalSize;
	}
	
	public long getUniqueVals(String att){
		try {
			Integer.parseInt(att);
		} catch (NumberFormatException e){
			Attribute att1 = getAttributeByName(att);
			if (att1 == null) return 0;
			return att1.getDistinct();
		}
		return 1;
	}
	
	public boolean hasAttributeWithName(final String attributeName) {
		
		//See if it has the . in it
		if (attributeName.contains(".")) {
			String [] split = attributeName.split("\\.");
			if (!split[0].equalsIgnoreCase(this.relationname)) {
				return false;
			}
			return this.hasAttributeWithName(split[1]);
		}
		
		//See if any of the attributes have this name
		for (int index = 0; index < attributes.size(); index++) {
			Attribute currentAttribute = attributes.get(index);
			if (attributeName.equalsIgnoreCase(currentAttribute.getName())) {
				return true;
			}
		}
		return false;
	}
	
	/**This method returns the index of the attribute in this relation with the
	 * global attribute ID specified.
	 * @param attributeID The global ID of the attribute specified.
	 * @return The index of the requested attribute in this relation.
	 */
	public int indexOfAttribute(long attributeID) {
		
		for (int index = 0; index < attributes.size(); index++) {
			//If the ID of this attribute and the specified ID are the same
			//then return index
			Attribute currentAttribute = attributes.get(index);
			if (currentAttribute.getID() == attributeID) {
				return index;
			}
		}
		System.out.println("Relation doesn't have the attribute with the"
				+ " specified ID.");
		return -1;		
	}
	
	/**This method calculates whether or not the last block of the relation is
	 * full and returns the result.  It looks to see if the total number of
	 * blocks * the records per block is equal to the total number of records.
	 * @return Whether or not the last block is full.
	 */
	public boolean isLastBlockFull() {
		return ((blockTotal * this.getRecordsPerBlock()) == records);
	}
	
	/**This method opens up an Iterator for this relation.
	 * @return An instance of Iterator for this relation.
	 */
	public Iterator open() {
		return new Iterator(this);	
	}
	
	/**This method takes in a block from this relation, parses out, an returns
	 * all records in the block.
	 * @param block The block to parse for records.
	 * @return The list of records and their attributes as a 2-D array.
	 */
	public String [][] parseBlock(ByteBuffer block) {
		//Loop through the block and determine the number of records
		//Look at the first byte of each record and stop when we find a 
		//blank one.
		int recordsFound = 0;
		//Dupe the block
		ByteBuffer duplicate = block.duplicate();
		byte [] blockArray = duplicate.array();
		
		//Loop, making sure we don't go beyond the block
		while (recordsFound < getRecordsPerBlock()) {
			
			//Calculate the offset for this cycle
			int offset = recordsFound * getSize();
		
			//End the loop unless a new record is found
			boolean lastRecord = true;
			
			//End this if the next record is out of the block
			if ((recordsFound * getSize()) + getSize() 
				> block.capacity()) {
				break;
			}
			
			//If the next record is still in the block, then read it.
			//System.out.println("Getting record " + recordsFound 
			//		+ " at byte " + recordsFound * getSize());
			byte [] record = new byte [getSize()];
			//Copy them over from blockArray
			for (int index = offset;  index < offset + getSize(); index++) {
				record [index - offset] = blockArray[index];
			}
 		
			//If this is the last record then stop the loop
			for (int index = 0; index < record.length; index ++) {
				if ((char) record[index] != '\0') {
					lastRecord = false;
					recordsFound++;
					break;
				}
			}
			
			if (lastRecord) {
				//System.out.println("All records in block found...");
				break;
			}		
		} //End looking for records
		
		//ystem.out.println("Found " + recordsFound + " records");
		
		//Now parse the records and add them to the list to return
		String [][] recordList = new String [recordsFound][attributes.size()];
		for (int currentRecord = 0; currentRecord < recordsFound;
			currentRecord++) {
			//Load records and parse them into their places
			byte [] record = new byte [getSize()];
			//Copy them over from blockArray
			int offset = currentRecord * getSize();
			for (int index = offset;  index < offset + getSize(); index++) {
				record [index - offset] = blockArray[index];
			}
			//Add the record to the list of records
			recordList[currentRecord] = parseRecord(ByteBuffer.wrap(record));
		}
		
		//Return the parsed records
		return recordList;
	}
	
	 public String [] parseRecord(final ByteBuffer record) {
		
		//Create the array of Strings that will hold the attributes from this
		String [] parsed = new String [attributes.size()];
		String parsedAttribute = null;
		
		//System.out.println();
		//Start at the first byte of this record, cause we should be realtively
		//sure the record starts there.
		int start = 0;
		
		//For each attribute in this relation, parse it out of this record
		for (int attributeID = 0; 
			attributeID < attributes.size(); attributeID++) {
	    	//Found what kind of attribute the current one is
			Attribute currentAttribute = attributes.get(attributeID);
			
			//Then get the bytes for the attribute
			if (currentAttribute.getType() == Attribute.Type.Int) {
				//System.out.println("Parsing int starting at " + start
				//		+ " and of size " + Attribute.INT_SIZE);
				parsedAttribute = Integer.toString(record.getInt(start));
				//System.out.println("Found int " + parsedAttribute);
			} else if (currentAttribute.getType() == Attribute.Type.Char) {
				parsedAttribute = parseString(record, start, 
						currentAttribute.getSize());
			} else if (currentAttribute.getType() == Attribute.Type.Long) {
				parsedAttribute = Long.toString(record.getLong(start));
			} else if (currentAttribute.getType() == Attribute.Type.Float) {
				parsedAttribute = Float.toString(record.getFloat(start));
			} else if (currentAttribute.getType() == Attribute.Type.Double) {
				parsedAttribute = Double.toString(record.getDouble(start));
			}
			
			//Then increment start by the size of this attribute so we get the
			//next one
			start += currentAttribute.getSize();
			
			//Add the parsedAttribute to the array to return
			parsed[attributeID] = parsedAttribute;
	    	
		} 
		
		return parsed;
	}
	
    public void setBlocktotal(int blocktotal) {
		modifydate = (new Date()).getTime();
		this.blockTotal = blocktotal;
	}
	
	public void setChannel(FileChannel channel) {
		this.channel = channel;
	}
	
	public void setCreationdate(long creationdate) {
		modifydate = (new Date()).getTime();
		this.creationdate = creationdate;
	}
	
	public void setIndexed(ArrayList<Integer> indexed) {
		modifydate = (new Date()).getTime();
		this.indexed = indexed;
	}
	
	public void setIndexfiles(ArrayList<String> indexfiles) {
		modifydate = (new Date()).getTime();
		this.indexFiles = indexfiles;
	}
	
	public void setModifydate(long modifydate) {
		
		this.modifydate = modifydate;
	}

	public void setRecords(int tuples) {
		 modifydate = (new Date()).getTime();
		this.records = tuples;
	}
	
	
	public void setRelationname(String relationname) {
    	modifydate = (new Date()).getTime();
		this.relationname = relationname;
	}
	
	public String toString(){
		return "Relation "+ID+" named: "+this.filename+" with "+attributes.size()
			+" attributes: "+attributes.toString()+"\n";
	}

	/**This will return a ByteBuffer representation of the metadata for this 
	 * relation.
	 * @return A byte buffer containing the schema of this relaion.
	 */
	public ByteBuffer writeCrapToBuffer() {
		ByteBuffer entry = 
			ByteBuffer.wrap(new byte[SystemCatalog.REL_REC_SIZE]);
		int currentPosition = 0;
		
		for (int j = 0; j < 15; j++){
			if (j < relationname.length()){
				char character = relationname.charAt(j);
				entry.putChar(j * 2, character);
			} else {
				entry.putChar(j * 2,BufferManager.NULL_CHARACTER);
			}
			currentPosition += Attribute.CHAR_SIZE;
			//System.out.println("Currently writing at " + currentPosition);
		}
		
		//Write all of the information about this relation
		entry.putInt(currentPosition, ID);
		currentPosition += Attribute.INT_SIZE;
		entry.putLong(currentPosition, creationdate);
		currentPosition += Attribute.LONG_SIZE;
		entry.putLong(currentPosition, modifydate);
		currentPosition += Attribute.LONG_SIZE;
		entry.putInt(currentPosition, records);
		currentPosition += Attribute.INT_SIZE;
		entry.putInt(currentPosition, blockTotal);
		currentPosition += Attribute.INT_SIZE;
		//System.out.println("Currently writing at " + currentPosition);
		
		//Write all of the indexed attributes
		int index = 0;
		for (index = 0; index < indexed.size(); index++) {
			entry.putInt(currentPosition, indexed.get(index).intValue());
			currentPosition += Attribute.INT_SIZE;
			//System.out.println("Currently writing at " + currentPosition);
		} 
		//Then blank space for them
		while (index < 10){
			entry.putInt(currentPosition, -1);
			index++;
			currentPosition += Attribute.INT_SIZE;
			//System.out.println("Currently writing at " + currentPosition);
		}
		
		//Now write the names of all of the index files, for some reason
		for (index = 0; index < indexFiles.size(); index++) {
			for (int character = 0; character < 15; character++) {
				if (character < indexFiles.get(index).length()) {
					char ch = indexFiles.get(index).charAt(character);
					entry.putChar(currentPosition, ch);
					currentPosition += Attribute.CHAR_SIZE;
				} else {
					entry.putChar(currentPosition, 
						BufferManager.NULL_CHARACTER);
					currentPosition += Attribute.CHAR_SIZE;
				}
				//System.out.println("Currently writing at " + currentPosition);
			}
		}
		
		
		while (index < 10) {
			index++;
			for (int character = 0; character < 15; character++) {
				entry.putChar(currentPosition, 
					BufferManager.NULL_CHARACTER);
				//System.out.println("Writing index " + index + " and character "
				//	+ character);
				currentPosition += Attribute.CHAR_SIZE;
			}
		}
		//System.out.println("Finished writing indexe names at " 
		//	+ currentPosition);
		
		//System.out.println("Wrote relation data for " 
		//		+ this.relationname + "...");
		return entry;
	}
	
	/**This method will write the given string to the given block a the given
	 * position, and will fill in the rest of the space for this attribute
	 * with '\0'.  
	 * @param block The block to write these to.
	 * @param chars The string of characters to write.
	 * @param start Where this CHAR should be written in the block.
	 * @param newSize This size of this CHAR() attribute, in bytes.
	 * @return Whether or not the write was successful.
	 */
	public boolean writeString(final ByteBuffer block, 
		final String chars, final int start, final int newSize) {
		int offset = start;
		//System.out.println("Offset: " + offset); 
		//Loop through the String, writing the characters as ints.
		for (int index = 0; index < chars.length();  index++) {
			//Get the char at the index
			//System.out.println(block);
			block.putChar(offset, chars.charAt(index));
			//System.out.println("Putting character " + chars.charAt(index)
			//	+ " at " + offset);
			//Then add the size of an int to start
			offset += Attribute.CHAR_SIZE;
		}
		//After all of the members of the string have been written, fill
		//in the rest of the space with '\O'
		int stringSize = chars.length() * Attribute.CHAR_SIZE;
		int nullsToWrite = (newSize - stringSize) / 2;
		for (int nulls = 0; nulls < nullsToWrite; nulls++) {
			block.putChar(offset, BufferManager.NULL_CHARACTER);
			//System.out.println("Putting  null character at " + offset);
			offset += Attribute.CHAR_SIZE;
		}
		
		//Then return true;
		return true;
	}

	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<Attribute> attributes) {
		this.attributes = attributes;
	}
	
}
