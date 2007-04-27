/*
 * SystemCatalog.java
 *
 * Created on March 6, 2007, 5:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package dbase;


/**
 *
 * @author andrewco
 */

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class SystemCatalog {
	
	
    
	public static final String SELECT_ATTRIBUTE_CATALOG 
		= "ATTRIBUTE_CATALOG.ac";
	
	public static final String SELECT_RELATION_CATALOG = "RELATION_CATALOG.rc";
	
	public static final int MAX_NAME_LENGTH = 15;
	
	public static final int MAX_NAME_SIZE = MAX_NAME_LENGTH 
		* Attribute.CHAR_SIZE;
	
    public final static long ATT_OFFSET = (long)Math.pow(2, 31);
    
    public static final int ATT_REC_SIZE = 58;
    
    public final static long REL_OFFSET = (long)Math.pow(2, 30);
    
    public static final int REL_REC_SIZE = 398;
    /**String Length Maximum*/
    public final static int stringlength = 15;
    private String attributecatalog = "ATTRIBUTE_CATALOG.ac";
    /**A list of Relations and Attributes */
    private ArrayList <Attribute> attributes;
    /**The Database's buffer */
    public BufferManager buffer;
    /** FileChannels for the attribute and relation catalogs*/
    private String relationcatalog = "RELATION_CATALOG.rc";
    /** Singleton relationholder */
    private RelationHolder relationHolder = RelationHolder.getRelationHolder();
    
    long relationmarker = 0, attributemarker = 0;
    
    /** Creates a new instance of SystemCatalog */
    public SystemCatalog() {
    	attributes = new ArrayList<Attribute>();
    	buffer = BufferManager.getBufferManager();
    	loadRelationCatalog();
    	loadAttributeCatalog();
    }
    
    private void loadAttributeCatalog() {
    	
    	ByteBuffer block;
    	int blockPosition = 0;
    	int catalogBlocks = 0;
    	
    	//Try to find the size of the relation catalog so we know how many
    	//blocks to load
    	try {
    		FileChannel relationCatalog = 
    			StorageManager.openFile(relationcatalog);
    		//System.out.println("Loaded em again");
    		catalogBlocks = 
    			(int) (relationCatalog.size() 
    			/ (int) StorageManager.BLOCK_SIZE);
    		relationCatalog.close();
    		//System.out.println("Closed em.");
    	} catch (IOException e) {
    		System.out.println("Couldn't read relation catalog file size.");
    		System.out.println("Exiting...");
    		System.exit(1);
    	}
    		
    	
    	for (int currentBlock = 0; currentBlock < catalogBlocks;
    		currentBlock++) {
    		
    		//Load the current block from the attribute catalog and dup it
    		block = buffer.readAttributeCatalog(attributecatalog,
    				currentBlock);
    		block = block.duplicate();
    		blockPosition = 0;

    		//Loop through all of the records in the block
    		for (int currentRecord = 0; currentRecord 
	    		< StorageManager.BLOCK_SIZE / ATT_REC_SIZE; currentRecord++) {

    			//Check the entire length of the record.  If there is one
    			//non-null, there is a record
    			ByteBuffer nullCheckBuffer = block.duplicate();
    			boolean lastRecord = true;
    			for (int index = blockPosition; 
    			index < blockPosition + ATT_REC_SIZE; index++) {
    				if ((char) (nullCheckBuffer.get(index)) != '\0') {
    					lastRecord = false;
    				}
    			}
    			if (lastRecord) {
    				break;
    			}

    			//Load the name of the attribute
    			String name = Relation.parseString(block, blockPosition,
    				MAX_NAME_SIZE);
    			blockPosition += MAX_NAME_SIZE;
    			long attributeID = block.getLong(blockPosition);
    			blockPosition += Attribute.LONG_SIZE;
    			char nullable = block.getChar(blockPosition);
    			blockPosition += Attribute.CHAR_SIZE;
    			Attribute.Type type = Attribute.charToType(
    					block.getChar(blockPosition));
    			blockPosition += Attribute.CHAR_SIZE;
    			long rID = block.getLong(blockPosition);
    			blockPosition += Attribute.LONG_SIZE;
    			int distinct = block.getInt(blockPosition);
    			blockPosition += Attribute.INT_SIZE;
    			int length = block.getInt(blockPosition);
    			blockPosition += Attribute.INT_SIZE;

    			Attribute used = 
    				new Attribute(name, attributeID, rID, type, 
    						nullable, distinct, length);
    			//System.out.println("" + used.getType());
    			attributes.add(used);
    			System.out.println("Loading attribute " + name 
    					+ " to relation " 
    					+ rID);
    			relationHolder.getRelation(rID).addAttribute(used);

    		} //End block search
    	} //End block loading
    }
    
    private void loadRelationCatalog() {

    	//A pointer to the buffer manager for reads
    	buffer = BufferManager.getBufferManager();
    	//The number of blocks in the catalogs
    	int catalogBlocks = 0;
    	
    	
    	//Try to find the size of the relation catalog so we know how many
    	//blocks to load
    	try {
    		FileChannel relationCatalog = 
    			StorageManager.openFile(relationcatalog);
    		//System.out.println("Loaded em again");
    		catalogBlocks = 
    			(int) (relationCatalog.size() 
    			/ (int) StorageManager.BLOCK_SIZE);
    		relationCatalog.close();
    		//System.out.println("Closed em.");
    	} catch (IOException e) {
    		System.out.println("Couldn't read relation catalog file size.");
    		System.out.println("Exiting...");
    		System.exit(1);
    	}
 
    	//Loop through all the blocks in the relation catalog
    	for (int blockNumber = 0; blockNumber < catalogBlocks; blockNumber++) {
        	
        	//The ByteBuffer to hold the block in the relation catalog
        	ByteBuffer block;
        	//Start at the first relation metadata record in each block
        	int blockPosition = 0;

    		//Load the block for this relation and duplicate it
    		block = buffer.readRelationCatalog(relationcatalog, blockNumber);
    		//Duplicate the block so that we don't actually remove things
    		//from the buffer
    		ByteBuffer duplicateBlock = block.duplicate();
    		
    		//Print the block we are loading relation data from 
    		System.out.println("Loading relation data from block " + blockNumber
    			+ "...");
    		
    		//System.out.println("Error Isn't Here.");
    		for (int recordNumber = 0; recordNumber 
    			< StorageManager.BLOCK_SIZE / REL_REC_SIZE; 
    			recordNumber++) {
    			
    			//The name of the relation we are loading
    			String relationName = "";
    			
    			//Make sure that we aren't out of records by checking the entire
    			//length of the record.  If there is one
    			//non-null, there is a record
    			ByteBuffer nullCheckBuffer = duplicateBlock.duplicate();
    			boolean lastRecord = true;
    			for (int index = blockPosition; 
    				index < StorageManager.BLOCK_SIZE; index++) {
    				if ((char) (nullCheckBuffer.get(index)) != '\0') {
    					lastRecord = false;
    					break;
    				}
    			}
    			if (lastRecord) {
    				System.out.println("Loaded all relation data from block "
    					+ blockNumber + "...");
    				break;
    			}
    			
    			//Say which record we are loading
    			//System.out.println("Loading relation " + relationmarker
    			//	+ " at block " + 1 + " and byte " + relationpos);
    			
    			relationName = Relation.parseString(duplicateBlock, 
    				blockPosition, MAX_NAME_SIZE);
    			blockPosition += MAX_NAME_SIZE;
				//System.out.println("Loading relation name at " 
    			//		+ relationpos);
    			
    			//Load the relation and name and add it to the catalog
    			int relationID = duplicateBlock.getInt(blockPosition);
    			blockPosition += Attribute.INT_SIZE;
    			Relation newRelation = new Relation(relationName, relationID);
    			relationHolder.addRelation(newRelation);
    			//Print that we loaded the relation.
    			System.out.println("Loaded relation " + relationName + "...");
    			newRelation.setCreationdate(
    				duplicateBlock.getLong(blockPosition));
    			blockPosition += Attribute.LONG_SIZE;
    			newRelation.setModifydate(
    				duplicateBlock.getLong(blockPosition));
    			blockPosition += Attribute.LONG_SIZE;
    			newRelation.setRecords(duplicateBlock.getInt(blockPosition));
    			blockPosition += Attribute.INT_SIZE;
    			newRelation.setBlocktotal(duplicateBlock.getInt(blockPosition));
    			blockPosition += Attribute.INT_SIZE;

    			//System.out.println("Relation attributes loaded at byte " 
        		//		+ relationpos);
    			
    			//Load which attributes are indexed
    			for (int attribute = 0; attribute < 10; attribute++) {
    				//If the index isn't -1, then that attribute is indexed
    				if (duplicateBlock.getInt(blockPosition) != -1) {
    					newRelation.addIndex(
    						duplicateBlock.getInt(blockPosition));
    				}
					blockPosition += Attribute.INT_SIZE;
    			}
    			//System.out.println("Indexed attributes loaded at byte " 
    			//	+ relationpos);
    			
    			//Load the names of the indexes
    			String indexName = "";
    			for (int currentIndex = 0; currentIndex < 10; currentIndex++) {
    				indexName = Relation.parseString(duplicateBlock, 
    					blockPosition, MAX_NAME_SIZE);
    				blockPosition += MAX_NAME_SIZE;
    				newRelation.addIndex(indexName);
    			}
    			//System.out.println("Index names loaded at byte " 
        		//		+ relationpos);
    		} //End the relation loading loop
    	} //End the block loading loop	
    } //End loadRelationCatalog
    
    /**
     * 
     * @param cstmt the statement to parse
     * @return if successfully creates the index
     */
    public boolean createIndex(String cstmt){
    	String relationname = cstmt.substring(cstmt.toLowerCase().indexOf(" on ")+4,cstmt.indexOf("(")).trim();
    	String indexname = cstmt.substring(12,cstmt.toLowerCase().indexOf(" on ")).trim();
    	String attribute = cstmt.substring(cstmt.indexOf("(")+1, cstmt.indexOf(")"));
    	boolean dups = (cstmt.toLowerCase().indexOf("no duplicates") != -1);
    	return createIndex(relationname, attribute, indexname, dups);
    	
    }
    
    /**
     *Creates an index on an existing relation.
     *
     *@param relation the relation to be indexed.
     *@param attribute the attribute to be indexed on.
     *
     *@return whether the index was created successfully
     */
    public boolean createIndex(String relationName, String attributeName,
    		String indexName, boolean duplicates) {
        long relationID = -1, attributeID = -1;
    	int i, j;
    	Relation relation;
    	Attribute.Type attributeType = Attribute.Type.Undeclared;
    	Attribute attribute = null;
    	Iterator iterator;
    	BTree bTree = new BTree();
    	
//    	System.out.println("This had better get called");
    	
    	StorageManager.openFile("" + indexName + ".if");
    	
    	int index;
        ArrayList <Relation> relations = relationHolder.getRelations();
        //Find the rID and aID for use with the indexing.
    	relationID = relationHolder.getRelationByName(relationName);
    	relation = relationHolder.getRelation(relationID);
        
    	//Find the attribute
        for (j = 0; j < attributes.size(); j++) {
        	if (attributes.get(j).getName().equalsIgnoreCase(attributeName) 
        		&& attributes.get(j).getParent() == relationID) {
        		attributeID = attributes.get(j).getID();
        		attributeType = attributes.get(j).getType();
        		attribute = attributes.get(j);
        		break;
        	}
        }
        
        //If either attribute of relation is null, return false
        if (relation == null || attribute == null) { 
        	System.out.println("Couldn't create index " + indexName
        		+ " on relation " + relationName);
        }
        
        //if it works open the appropriate type of index, duplicate or not
        if (relation == null || relation.containsIndex(""+indexName)){
        	return false;
        } else if (attributeType !=Attribute.Type.Int 
        		&& attributeType!=Attribute.Type.Long){
        	//We only index long and int, no chars
        	return false;
        } else{
	        if (duplicates) {
	        	//Open an index that does allow duplicates
	        	relation.addIndex(indexName);
	        	attribute.setIndex(indexName.toCharArray());
	        	index = bTree.OpenIndex(""+indexName+".if", true);
	        	iterator = relationHolder.getRelation(relationID).open(); 
        	} else {
        		//Open an index that doesn't allow duplicates
        		relation.addIndex(indexName);
            	attribute.setIndexd(indexName.toCharArray());
            	index = bTree.OpenIndex(""+indexName+".if", false);
            	iterator = relationHolder.getRelation(relationID).open();
        	}     	
        }
        
        
        //Iterate through the Relation and insert the values into the index.
        String[] record;
        String keyString;
        long key;
        long address;
        while(iterator.hasNext()) {
        	
        	record = iterator.getNext();
        	keyString = record[relation.indexOfAttribute(attributeID)];
        	key = Long.parseLong(keyString);
        	address = iterator.getAddress();
        	bTree.Insert(index, key, address);
        	System.out.println("Inserting index value " + key 
        		+ " with address " + address);
        	
        }
        bTree.CloseIndex(index);
        iterator.close();       
        return true;
    }
    
    /**
     *Creates a new table in the Catalog as well as a file for it.
     *@param relation the name and atributes still in code format
     *@param key the attribute to sort by possibly, probably not
     * (may be removed)
     *@return whether the table was created correctly.
     */
    public boolean createTable(final String relation, final String key) {
    	//Parse out the name of the relation from the statement
    	String relationName;
    	relationName = relation.substring(
    		relation.indexOf(" ", relation.toLowerCase().indexOf("table"))
    		+ 1, relation.indexOf("(")).trim();
    	
    	//If a table with this name already exists, print the error and return
    	//false
    	if (relationHolder.getRelationByName(relationName) != -1) {
    		System.out.println("Table " + relationName
    			+ " already exists...");
    		return false;
    	}
    	
    	//Get the ID of this new relation from relation holder, create a 
    	//new relation with it and add it to relationHolder and add it to the
    	//catalog
    	Relation newRelation = new Relation(relationName, 
    			relationHolder.getSmallestUnusedID());
    	System.out.println("Adding new relation " + newRelation.getName()
    			+ "...");
    	relationHolder.addRelation(newRelation);
    	writeoutRel(newRelation.writeCrapToBuffer(), newRelation.getID());
    	
    	//Find out where the ()'s enclosing the list of attributes is
    	//Start getting all of the attributes out of the statement
    	StringTokenizer st = new StringTokenizer(
    		relation.substring(relation.indexOf("(") + 1,
    		relation.indexOf(")")), 
    		",");
    	
    	//While there are more attributes in the list, add them to the relation
    	while (st.hasMoreTokens()) {

    		//Get the next attribute definition out of the tokenizer and 
    		//find its name, type and size
    		String currentAttribute = st.nextToken().trim();
    		String attributeName = currentAttribute.split(" ")[0];
    		String attributeType = currentAttribute.split(" ")[1];
    		int size = 0;
    		
    		//Get the type and create the new attribute for it, checking for the
    		//size if it turns out to be char.
    		Attribute.Type newAttributeType 
    			= Attribute.stringToType(attributeType);
    		if (newAttributeType == Attribute.Type.Char) {
    			size = Integer.parseInt(currentAttribute.split(" ")[2]);
    		}
    		
    		//Create the new attribute and add it to the list of attributes
    		//Create it with 0 distinct values as it is entirely new.
    		System.out.println("Adding attribute " + attributeName 
    				+ " to relation " + newRelation.getName() + "...");
    		Attribute newAttribute = new Attribute(attributeName,
    				getSmallestUnusedAttributeID(), newRelation.getID(),
    				newAttributeType, 't', 0, size);
    		newRelation.addAttribute(newAttribute);
    		attributes.add(newAttribute);
    		
    		//Write the new attribute to the catalog
    		writeOutAttribute(newAttribute);
    	}
    	
    	//System.out.println(relationHolder);
        return true;
    }
    
    
    /**This method will evaluate an expression passed to it for truth.
     * @param leftHandSide The left hand side of the operation.
     * @param rightHandSide The right hand side of the operation.
     * @param operation The comparison operator.
     * @return Whether or not the comparison is true;
     */
    private boolean evaluateWhereClause(final String leftHandSide,
    	final String rightHandSide, final String operation) {
    	
    	//Trim all of the things
    	String leftHandTrimmed = leftHandSide.trim();
    	String rightHandTrimmed = rightHandSide.trim();
    	String operationTrimmed = operation.trim();
    	
    	
    	//System.out.println("Evaluating " + leftHandTrimmed + " " 
    	//	+ operationTrimmed + " " + rightHandTrimmed);
    	
    	//Now go through the possible operations
    	if (operationTrimmed.equalsIgnoreCase("=")) {
    		return (leftHandTrimmed.equalsIgnoreCase(rightHandTrimmed));
    	} else if (operationTrimmed.equalsIgnoreCase(">")) {
    		return (Integer.parseInt(leftHandTrimmed) 
    			> Integer.parseInt(rightHandTrimmed));
    	} else if (operationTrimmed.equalsIgnoreCase("<")) {
    		return (Integer.parseInt(leftHandTrimmed) 
        		< Integer.parseInt(rightHandTrimmed)); 
    	}
    	
    	return false;	
    }
    
    
    /**
     *Returns an Iterator on the given relation.
     *
     *@param relation the relation to be used
     *
     *@return The Iterator
     */
      public Iterator getIterator(String relation) {
    	int ID = relationHolder.getRelationByName("relation");
    	if (ID == -1){
    		return null;
    	}
    	Relation rel = relationHolder.getRelation(ID);
    	if (rel == null){
    		return null;
    	}
    	
        return rel.open();
    }
    
    /**
	 * Gets a relation based on the Internal attribute ID as defined in RELATION_CATALOG
	 * @param ID The Internal ID
	 * @return The denoted Attribute
	 */
	public Attribute getAttribute(int ID) {
		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).getID() == ID) {
				return attributes.get(i);
			}
		}
		//TODO Possibly add code to load this Relation if not already done, we'll see.
		return null;
	}
    
    /**
	 * This method is used by SystemCatalog to assign IDs
	 * @return the smallest unused attribute ID
	 */
	public int getSmallestUnusedAttributeID(){
		int j=0;
		while (getAttribute(j)!=null){
			j++;
		}
		return j;
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
    	
    	//Ask relation which block this record should be written to, i.e. it's
    	//last block
    	Relation relation = relationHolder.getRelation(relationID);
    	long blockTotal = relation.getBlocktotal();
    	//The last block is blockTotal - 1 cause the first block is 0 
    	long lastBlock = blockTotal - 1;
    	
    	//See if there is enough space in the last block for another record.
    	if (relation.isLastBlockFull()) {
    		//If there isn't, generate a new block and write it to the file
    		//of the relation.
    		long blockAddress = BufferManager.makePhysicalAddress(relationID
    				, lastBlock + 1);
    		buffer.writePhysical(blockAddress, BufferManager.getEmptyBlock());
    		//Then increment the block count of the relation
    		relation.setBlocktotal(relation.getBlocktotal() + 1);
    	}

    	//Then regardless, the last block of the relation has enough space in
    	//it, so have the last block loaded into the buffer.
    	ByteBuffer block = buffer.read(relationID, 
    			relation.getBlocktotal() - 1);
    	//System.out.println("Inserting into block " 
    	//	+ buffer.makePhysicalAddress(relationID, 
    	//			relation.getBlockTotal() -1));
    	
    	//Then ask relation to insert the record in this block for us
    	if (record.toLowerCase().indexOf("values") < record.indexOf("(")) {
    		// there are no attributes given
    		relation.addRecord(block, record);
    	} else{
    		int oparan = record.indexOf("(");
    		int cparan = record.indexOf(")")+1;
    		String attlist = record.substring(oparan, cparan);
    		oparan = record.indexOf("(", cparan);
    		cparan = record.indexOf(")", oparan)+1;
    		String vallist = record.substring(oparan, cparan);
    		relation.addRecord(block, vallist, attlist);
    	}
    	
    	//Update the record for this relation
    	ByteBuffer entry = relation.writeCrapToBuffer();
    	writeoutRel(entry, relation.getID());
    	
    	
    	return true;
    }
    
    /**
     *Inserts a record into a relation and possibl an index.
     *
     *@param insertion the record to be inserted still in string format
     *
     *@return whether it was successfully added.
     */
    public boolean insert(String insertion) {
    	String relationname = insertion.split(" ")[2];
        int ID = relationHolder.getRelationByName(relationname);
        insert(ID, insertion);
    	return true;
    }
    
    /**This method will parse out the comparison against which the selection
     * is being made
     * @param whereClause The WHERE clause.
     * @return The comparison we are comparing the records against.
     */ 
    private String parseComparison(final String whereClause) {
    	return whereClause.split("\\s+")[3];
    }
    
    /**This method will parse out the operation (=, >, <) which is being used
     * in the select.
     * @param whereClause The WHERE clause.
     * @return The comparison we are comparing the records against.
     */ 
    private String parseComparisonOperation(final String whereClause) {
    	return whereClause.split("\\s+")[2];
    }
    
    private String parseConditionAttribute(final String condition) {
    	//TODO allow it to handle more than the word after the where.
    	//At this point it should be the second word
    	//System.out.println(condition);
    	return condition.split("\\s")[1]; 	
    }
    
    /**This method parses the attributes to be selected from a SELECT
     * statement and returns them an an array.
     * @param selection The SELECT statement.
     * @return The attributes requested from the SELECT.
     */
    public String [] parseSelectAttributes(final String selection) {
    	//Split up the selection statement
    	String [] commands = selection.split("\\s");
    	//And the return array
    	String selectAttributes = "";
    	
    	//Start on index 1 because that's where the attributes start
    	for (int word = 1; word < commands.length; word++) {
    		if (commands[word].equalsIgnoreCase("FROM")) {
    			break;
    		} else { 
    			selectAttributes = selectAttributes + " " + commands[word];
    		}
    	}
    	return selectAttributes.split("\\s");
    }
    
    /**This method parses out the table from which the selection is being
     * made and returns it.
     * @param selection The select statement.
     * @return The name of the table being selected from.
     */
    public String parseSelectTable(final String selection) {
    	//Fist thing to do is to split up the command on all spaces
    	String [] commands = selection.split("\\s+");
    	//Find the word "FROM" and we know the word after that
    	//is the TABLE key word, or the relation.
    	for (int index = 0; index < commands.length; index++) {
    		if (commands[index].equalsIgnoreCase("FROM")) {
    			//If TABLE is the word after from, the word after that is
    			//the relation, or if no TABLE then it is the relation
    			if (commands[index + 1].equalsIgnoreCase("TABLE")) {
    				return commands[index + 2];
    			} else {
    				return commands[index + 1];
    			}
    		}
    	}
    	return null;
    }
    
    /**This method parses and returns the WHERE clause from a SELECT
     * statement.
     * @param selection The select statement containing the where clause.
     * @return The WHERE clause.
     */
    public String parseWhereClause(final String selection) {
    	//First find the index of where
    	int whereIndex = selection.indexOf("WHERE");
    	
    	//Return the substring after that index
    	return selection.substring(whereIndex);
    }
    
    
    /**Returns metadata of the relations or attributes as specified by
     * the user.
     *@param selection The selection statement from the user.
     *@return The specified metada as an array of strings.
     */
    public String [] selectFromCatalog(final String selection) {
    	
    	//The return array
    	String [] catalogSelection = null;
    	
    	//First see which catalog they want to select from, attribute or
    	//relation.
    	String [] parsedSelection = selection.split("\\s");
    	if (parsedSelection[3].equalsIgnoreCase(SELECT_ATTRIBUTE_CATALOG)) {
    		catalogSelection = selectFromAttributeCatalog(selection);
    	} 
    	if (parsedSelection[3].equalsIgnoreCase(SELECT_RELATION_CATALOG)) {
			catalogSelection = selectFromRelationCatalog(selection);
		}

		return catalogSelection;
    }
    
    /**This method will return the specified selection from the Attribute
     * catalog, basically the attribute metadata.
     * @param selection The selection statement.
     * @return The metatdata from the attribute catalog.
     */
    private String [] selectFromAttributeCatalog(final String selection) {
    	//Open up a FileChannel to the attribute catalog and prepare to
    	//read things in.
    	FileChannel attCat = StorageManager.openFile(attributecatalog);
    	ByteBuffer block;
    	int position = 0;
    	
    	//The size of the attribute catalog, in blocks
    	int blocks = 0;
    	
    	//The array that will hold the return data
    	ArrayList <String> metaData = new ArrayList <String> ();
    	
    	//Try to find the size of the attribute catalog, so we know how 
    	//many blocks we have.  Then close the file
    	try {
    		blocks = (int) (attCat.size() 
    				/ (int) StorageManager.BLOCK_SIZE);
    	 	attCat.close();
    	} catch (IOException exception) {
    		System.out.println("Couldn't find the size of the attribute" 
    			+ " catalog.");
    		System.out.println("SystemCatalog.selectFromAttributeCatalog.");
    		System.out.println(exception.getStackTrace());
    		return null;
    	}
    	
    	//Now loop through the attribute catalog parsing things out.
    	for (int i = 0; i < blocks; i++) {
    		
    		//Get the block for the attribute catalog from the BufferManager
    		 block = buffer.readAttributeCatalog(attributecatalog, i);
    		 //System.out.println("Error Isn't Here Either.");
    		 
    		 //Loop through the entirety of this attribute block parsing things
    		 for (int j = 0; 
    		 	j < StorageManager.BLOCK_SIZE / ATT_REC_SIZE; j++) {
    			 
    			 //If we've come to the end of the attributes then break
    			 if (block.getChar(position) == BufferManager.NULL_CHARACTER) {
    				 break;
    			 }
    			 
    			 //First get the name of the attribute from the block
    			 String name = "";
    			 for (int k = 0; k < SystemCatalog.stringlength; k++) {
    				 if (block.getChar(position) 
    					!= BufferManager.NULL_CHARACTER) {
    					 name += block.getChar(position);
    				 }
    				 position += Attribute.CHAR_SIZE;
    			 }
    			 
    			 //Now get the other parts of the attribute out in a less
    			 //comples manor.
    			 long aID = block.getLong(position);
    			 position += Attribute.LONG_SIZE;
    			 char nullable = block.getChar(position);
    			 position += Attribute.CHAR_SIZE;
    			 char type = block.getChar(position);
    			 position += Attribute.CHAR_SIZE;
    			 long rID = block.getLong(position);
    			 position += Attribute.LONG_SIZE;
    			 int distinct = block.getInt(position);
    			 position += Attribute.INT_SIZE;
    			 int length = block.getInt(position);
    			 position += Attribute.INT_SIZE;
    			 
    			 //Now pack all of the attribute metadata into a string
    			 String data = name + " " + aID + " " + nullable + " " + type 
    			 	+ " " + rID + " " + distinct + " " + length;
    			 //Add the string to the array
    			 metaData.add(data);
    		 }
    		 position = 0;
    	 }	
        //Turn all of the attribute descriptions into a String []
    	String [] returnArray = new String [metaData.size()];
    	for (int index = 0; index < metaData.size(); index++) {
    		returnArray[index] = (String) metaData.get(index);
    	}
    	
    	//Now return the array
    	return returnArray;	
    }
    
    /**This method will return the specified selection from the Relation
     * catalog, basically the relation metadata.
     * @param selection The selection statement.
     * @return The metatdata from the relation catalog.
     */
    private String [] selectFromRelationCatalog(final String selection) {
    	//Open up a FileChannel to the relation catalog and prepare to
    	//read things in.
    	FileChannel relationCat = StorageManager.openFile(relationcatalog);
    	ByteBuffer block;
    	int position = 0;
    	
    	//The size of the relation catalog, in blocks
    	int blocks = 0;
    	
    	//The array that will hold the return data
    	ArrayList < String > metaData = new ArrayList < String > ();
    	
    	//Try to find the size of the relation catalog, so we know how 
    	//many blocks we have.  Then close the file
    	try {
    		blocks = (int) (relationCat.size() 
    				/ (int) StorageManager.BLOCK_SIZE);
    	 	relationCat.close();
    	} catch (IOException exception) {
    		System.out.println("Couldn't find the size of the relation" 
    			+ " catalog.");
    		System.out.println("SystemCatalog.selectFromRelationCatalog.");
    		System.out.println(exception.getStackTrace());
    		return null;
    	}
    	
    	//Now loop through the relation catalog parsing things out.
    	for (int i = 0; i < blocks; i++) {
    		
    		//Get the block for the attribute catalog from the BufferManager
    		 block = buffer.readRelationCatalog(relationcatalog, i);
    		 //System.out.println("Error Isn't Here Either.");
    		 
    		 //Loop through the entirety of this relation block parsing things
    		 for (int j = 0; j < StorageManager.BLOCK_SIZE / REL_REC_SIZE;
    		 	j++) {
    			 
    			 //Start off j records in on this block
    			 position = j * REL_REC_SIZE;
    			 
    			 //If we've come to the end of the relations then break
    			 if (block.getChar(position) == BufferManager.NULL_CHARACTER) {
    				 break;
    			 }
    			 
    			 //First get the name of the relation from the block
    			 String name = "";
    			 for (int k = 0; k < SystemCatalog.stringlength; k++) {
    				 if (block.getChar(position) 
    					!= BufferManager.NULL_CHARACTER) {
    					 name += block.getChar(position);
    				 }
    				 position += Attribute.CHAR_SIZE;
    			 }
    			 
    			 //Now get the other parts of the relation out in a less
    			 //comples manor.
    			 int ID = block.getInt(position);
    			 position += Attribute.INT_SIZE;
    			 long creationDate = (block.getLong(position));
    			 position += Attribute.LONG_SIZE;
    			 long modifyDate = (block.getLong(position));
    			 position += Attribute.LONG_SIZE;
    			 int records = (block.getInt(position));
    			 position += Attribute.INT_SIZE;
    			 int blocktotal = (block.getInt(position));
    			 position += Attribute.INT_SIZE;
    			 
    			 //Now pack all of the attribute metadata into a string
    			 String data = name + " " + ID + " " 
    			 	+ (new Date(creationDate)).toString() + " " 
    			 	+ " " + (new Date(modifyDate)).toString() + " " 
    			 	+ records + " " + blocktotal;
    			 
    			 //Add the string to the array
    			 metaData.add(data);
    		 }//End j loop
    		 position = 0;
    	 }//End i loop	
        //Turn all of the attribute descriptions into a String []
    	String [] returnArray = new String [metaData.size()];
    	for (int index = 0; index < metaData.size(); index++) {
    		returnArray[index] = (String) metaData.get(index);
    	}
    	
    	//Now return the array
    	return returnArray;
    }
    
    /**
    *Returns the rows from an Index
    *
    *@param selection the code from the user
    *
    *@return The Index in String format.
    */
   public String selectFromIndex(String selection) {
	   BTree bt = new BTree();
	   String[] splitter = selection.split(";")[0].split(" ");
	   String indexname = splitter[splitter.length-1];
	   String relationname = splitter[splitter.length-2];
	   Relation rel = relationHolder.getRelation(relationHolder.getRelationByName(relationname));
	   //rel.getIndexFileByName();

	   int idx = bt.OpenIndex(indexname, true);
	   //tring str = bt.;
	   bt.CloseIndex(idx);
	   return "POOP";
   }

	
	/**
     *Returns the rows from a Table
     *@param selection the code from the user
     *@return The Table in String format.
     */
    public String [] selectFromTable(String selection) {

    	//Parse out the desired data fields from the select
    	//String [] attributes = parseSelectAttributes(selection);
    	//for (int i = 0; i < attributes.length; i++) {
    	//	System.out.println(attributes[i]);
    	//}
    	
    	String total = "";
    	
    	//Parse out the desired table to work on and various parts of the
    	//Select statement
    	String table = parseSelectTable(selection);
    	//System.out.println("Selecting from table " + table);
    	String whereClause = parseWhereClause(selection);
    	//System.out.println(whereClause);
    	String conditionAttribute = parseConditionAttribute(whereClause);
    	//System.out.println("On attribute " + conditionAttribute);
    	//Get the variable, what we are comparing the attribute against
    	String comparison = parseComparison(whereClause);
    	//System.out.println(variable);
    	String operation = parseComparisonOperation(whereClause);
    	
    	//Get the relation that this is working on and the index of the
    	//Attribute under scrutiny
    	long relationID = relationHolder.getRelationByName(table);
    	Relation relation = relationHolder.getRelation(relationID);
    	//System.out.println(relationHolder);
    	int attributeIndex = relation.getIndexByName(conditionAttribute);
    	//System.out.println("At " + table + " index " + attributeIndex);
    	
    	//Now see which records of this relation match
    	Iterator iterator = relation.open();
    	
    	while(iterator.hasNext()) {
    		String [] values = iterator.getNext();
    		//Now just see if they are the same
        	//for (int i = 0; i < values.length; i++) {
        		//System.out.println(values[i]);
        	//}
    		//System.out.println(attributeIndex);
			String value = values[attributeIndex].trim();
			//System.out.println("Comparing " + value + " and "
    		//		+ variable);
    		if (evaluateWhereClause(value, comparison, operation)) {
    	    	//for (int i = 0; i < values.length; i++) {
    	    		//System.out.println( "Value: " + values[i]);
    	    	for (int i = 0; i < values.length; i++) {
    	    		total = total + values[i] + " ";
    	    	}
    	    	total = total + "\n";
    	    	//System.out.println("They match!");
    		} 		
    	}
    	
        return total.split("\\n");
    }
	
    //TODO severly beat whoever wrote this.  It is shit.
	private void writeoutRel(ByteBuffer entry, int rID) {
		
		//Find out which block this relations meatadata should go in
		int recordsPerBlock = StorageManager.BLOCK_SIZE / REL_REC_SIZE;
		//System.out.println("Records per block " + recordsPerBlock);
		long blockNumber = rID / recordsPerBlock;
		int recordInBlock = rID % recordsPerBlock;
		int offSet = recordInBlock * REL_REC_SIZE;
		
		//System.out.println("Writing relation information to block "
		//	+ blockNumber + " at offset " + offSet + "...");
		
    	//See if this relation record will start a new block
    	if (offSet == 0) {
    		buffer.writeRelCatalog(relationcatalog, blockNumber, 
    			BufferManager.getEmptyBlock());
    	}
		
		//Get the block to write this relations metadata to from the buffer
		ByteBuffer block = buffer.readRelationCatalog(
			relationcatalog, blockNumber);

		//Now actually write the relations data into the block
		byte [] array = entry.array();
		for (int index = 0; index < array.length; index++) {
			block.put(index + offSet, array[index]);
		}
		//System.out.println(block.asCharBuffer());
		
		buffer.writeRelCatalog(relationcatalog, blockNumber, block);
	}
	
	public boolean writeOutAttribute(Attribute attribute) {
		//Add the entry fo this attribute to the catalog. 
		
		//Find out which block this attributes meatadata should go in
		int recordsPerBlock = StorageManager.BLOCK_SIZE / ATT_REC_SIZE;
		//System.out.println("Records per block " + recordsPerBlock);
		long blockNumber = attribute.getID() / recordsPerBlock;
		int recordInBlock = (int) attribute.getID() % (int) recordsPerBlock;
		int offSet = recordInBlock * ATT_REC_SIZE;
		
		System.out.println("Writing attribute " + attribute.getID()
				+ " information to block "
				+ blockNumber + " at offset " + offSet + "...");

		//See if this attribute record will start a new block
		if (offSet == 0) {
			buffer.writeAttCatalog(attributecatalog, blockNumber, 
					BufferManager.getEmptyBlock());
		}
		
		//Get the block to write this relations metadata to from the buffer
		ByteBuffer block = buffer.readAttributeCatalog(
			attributecatalog, blockNumber);

		//Now actually write the attributes data into the block
		ByteBuffer entry = attribute.writeCrapToBuffer();
		byte [] array = entry.array();
		for (int index = 0; index < array.length; index++) {
			block.put(index + offSet, array[index]);
		}
		System.out.println("Attribute entry " + entry.asCharBuffer());
		System.out.println("Block " + block.asCharBuffer());
		
		buffer.writeAttCatalog(attributecatalog, blockNumber, block);
		
		return true;
	}
	
	
    
    public static void main(String[] args){
    	SystemCatalog sc = new SystemCatalog();

    	//Make a whole mess of tables
    	int tables = 20;
    	String tableNumber = "TABLE_";
    	/*for (int table = 0; table < tables; table++) {
        	sc.createTable("CREATE TABLE " + tableNumber + table 
        		+ " (INT" + table + " INT, CHAR" + table + " CHAR 25)", "key");
    	}
    	
    	//Insert shit into all of them
    	for (int table = 1; table <= tables; table++) {
    		for (int value = 0; value < 5; value++) {
    			String insert = "INSERT INTO " + tableNumber + table
    				+ " (CHAR" + table + ")" + " VALUES ("
    				+ value * table + ")";
    			sc.insert(insert);
    			System.out.println(insert);
    		}
    	}/*
    	
    	/*String [] result = sc.selectFromRelationCatalog("");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index] + " ");
		}
    	
    	for (int j = 0; j < 500; j++) {
    		String insert = "INSERT INTO TABLE_3 (INT3, CHAR3) VALUES (" + j + ", MONKEY)";
    		sc.insert(insert);
    	}*/
    	/*String [] result = sc.selectFromRelationCatalog("");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index] + " ");
		}*/
    	
    	/*for (int j = 0; j < 500000; j++) {
    		String insert = "INSERT INTO TABLE_4 (INT4, CHAR4) VALUES (" + j + ", MONKEY)";
    		sc.insert(insert);
    	}*/
		
		//sc.createIndex("CREATE INDEX IDX ON TABLE_2 (INT2) NO DUPLICATES");
    	for (int j = 0; j < 10; j++) {
    		String insert = "INSERT INTO TABLE_2 (INT2, CHAR2) VALUES (" + 10 + ", MONKEY)";
    		sc.insert(insert);
    	}
    	
		
    	/*System.out.println("All done");
    	String select = "SELECT * FROM TABLE_4 WHERE INT4 = 250000";
    	result = sc.selectFromTable(select);
		for (int index = 0; index < result.length; index++) {
			System.out.print(result[index]);
		}*/
    	
    	/*result = sc.selectFromRelationCatalog("");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index] + " ");
		}
		result = sc.selectFromAttributeCatalog("");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index] + " ");
		}*/
    	sc.buffer.flush();
    	
    	/*for (int table = 1; table <= tables; table++) {
    		String select = "SELECT * FROM TABLE " + tableNumber + table;
    		select = select + " WHERE CHAR" + table + " = ";
    		select = select + " " + 3 * table + "";
    		System.out.println(select);
    		String [] result = sc.selectFromTable(select);
    		for (int index = 0; index < result.length; index++) {
    			System.out.print(result[index]);
    		}
    		System.out.println();
    	}
    	
 		String [] result = sc.selectFromAttributeCatalog("");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index] + " ");
		}
		result = sc.selectFromRelationCatalog("");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index] + " ");
		}
		sc.createIndex("TABLE_1", "CHAR1", "WALRUS", true);
    	
		//Create some INT tables
    	for (int table = 1; table <= tables; table++) {
        	sc.createTable("CREATE TABLE " + "INT_TABLE_" + table 
        		+ " (INT" + table + " INT)", "key");
    	}
    	sc.createIndex("INT_TABLE_1", "INT1", "WALRUS", true);*/
    }
}
