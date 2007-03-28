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
import java.util.StringTokenizer;

public class SystemCatalog {
    
	public final static String SELECT_ATTRIBUTE_CATALOG = "ATTRIBUTE_CATALOG.ac";
	
	public final static String SELECT_RELATION_CATALOG = "RELATION_CATALOG.ac";
	
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
    	int relsize = 0, attsize = 0;
    	
    	try {
    		FileChannel relcat = StorageManager.openFile(relationcatalog);
    		FileChannel attcat = StorageManager.openFile(attributecatalog);
    		System.out.println("Loaded em again");
    		relsize = (int) (relcat.size() / (int) StorageManager.BLOCK_SIZE);
    		attsize = (int) (attcat.size() / (int) StorageManager.BLOCK_SIZE);
    		relcat.close();
    		attcat.close();
    		System.out.println("Closed em.");
    	} catch(IOException e) {
    		System.exit(1);
    	}
    	ByteBuffer relbuffer;
    	int relationpos = 0;
    	String relname = "";
    	int ID;
    	//Relation rel;
    	for(int i = 0; i < relsize; i++) {

    		//Load the block for this relation and duplicate it
    		relbuffer = buffer.readRelationCatalog(relationcatalog, 
    				(relationmarker * REL_REC_SIZE) 
    				/ StorageManager.BLOCK_SIZE);
    		relbuffer = relbuffer.duplicate();
    		
    		//Find the position within this block
    		relationpos = ((int) relationmarker * REL_REC_SIZE)
    			% StorageManager.BLOCK_SIZE;
    		
    		//System.out.println("Error Isn't Here.");
    		for(int j = 0; j < StorageManager.BLOCK_SIZE / REL_REC_SIZE; j++) {
//    			Check the entire length of the record.  If there is one
    			//non-null, there is a record
    			ByteBuffer nullCheckBuffer = relbuffer.duplicate();
    			boolean lastRecord = true;
    			for (int index = relationpos; 
    				index < relationpos + ATT_REC_SIZE; index++) {
    				if ((char) (nullCheckBuffer.get(index)) != '\0') {
    					lastRecord = false;
    				}
    			}
    			if (lastRecord) {
    				System.out.println("Loaded all relation data...");
    				break;
    			}
    			
    			//Say which record we are loading
    			//System.out.println("Loading relation " + relationmarker
    			//	+ " at block " + 1 + " and byte " + relationpos);
    			
    			for (int k = 0; k < 15; k++) {
    				if (relbuffer.getChar(relationpos) 
    					!= BufferManager.NULL_CHARACTER) {
    					relname += relbuffer.getChar(relationpos);
    				}
    				relationpos += Attribute.CHAR_SIZE;
    			}
    			
    			ID = relbuffer.getInt(relationpos);
    			//System.out.println(ID);
    			Relation rel = new Relation(relname, ID);
    			relationHolder.addRelation(rel);
    			//Print that we loaded the relation.
    			System.out.println("Loaded relation " + relname + "...");
    			relationpos += Attribute.INT_SIZE;
    			rel.setCreationdate(relbuffer.getLong(relationpos));
    			relationpos += Attribute.LONG_SIZE;
    			rel.setModifydate(relbuffer.getLong(relationpos));
    			relationpos += Attribute.LONG_SIZE;
    			rel.setRecords(relbuffer.getInt(relationpos));
    			relationpos += Attribute.INT_SIZE;
    			rel.setBlocktotal(relbuffer.getInt(relationpos));
    			relationpos += Attribute.INT_SIZE;
    			relname = "";
    			String index = "";
    			
    			//Load which attributes are index
    			for(int l = 0; l < 10; l++) {
    				if (relbuffer.getInt(relationpos) != -1){
    					rel.addIndex(relbuffer.getInt(relationpos));
    				}
					relationpos += Attribute.INT_SIZE;
    			}
    			//Load the names of the indexes
    			for (int m = 0; m < 10; m++) {
    				for (int n = 0; n < 15; n++) {
    					if (relbuffer.getChar(relationpos) 
    						!= BufferManager.NULL_CHARACTER) {
    						index += relbuffer.getChar(relationpos);
    					}
    					relationpos += Attribute.CHAR_SIZE;
    				}
    				rel.addIndex(index);
    				index = new String();
    			}
    			
    			//go to the next relation
    			relationmarker++;	
    			
    		}
    		//Start at position 0 for the next block
    		relationpos = 0;
    	}
    	ByteBuffer attbuffer;
    	int attposition = 0;
    	 for (int i = 0; i < attsize; i++) {
    		 attbuffer = buffer.readAttributeCatalog(
    			attributecatalog, 
    			(attributemarker * ATT_REC_SIZE) / StorageManager.BLOCK_SIZE);
    		 attbuffer = attbuffer.duplicate();
//    		 System.out.println("Error Isn't Here Either.");
    		 for (int j = 0; j < StorageManager.BLOCK_SIZE / ATT_REC_SIZE; j++) {

    			 //Check the entire length of the record.  If there is one
    			 //non-null, there is a record
    			 ByteBuffer nullCheckBuffer = attbuffer.duplicate();
    			 boolean lastRecord = true;
    			 for (int index = attposition; 
    			 	index < attposition + ATT_REC_SIZE; index++) {
    				if ((char) (nullCheckBuffer.get(index)) != '\0') {
    					lastRecord = false;
    				}
    			 }
    			 if (lastRecord) {
    				 break;
    			 }
    			 
    			 String name = "";
    			 for (int k = 0; k < 15; k++) {
    				 if (attbuffer.getChar( attposition) != BufferManager.NULL_CHARACTER) {
    					 name += attbuffer.getChar(attposition);
    				 }
    				 attposition += Attribute.CHAR_SIZE;
    			 }
    			 long aID = attbuffer.getLong(attposition);
    			 attposition += Attribute.LONG_SIZE;
    			 char nullable = attbuffer.getChar(attposition);
    			 attposition += Attribute.CHAR_SIZE;
    			 Attribute.Type type = Attribute.charToType(
    					 attbuffer.getChar(attposition));
    			 attposition += Attribute.CHAR_SIZE;
    			 long rID = attbuffer.getLong(attposition);
    			 attposition += Attribute.LONG_SIZE;
    			 int distinct = attbuffer.getInt(attposition);
    			 attposition += Attribute.INT_SIZE;
    			 int length = attbuffer.getInt(attposition);
    			 attposition += Attribute.INT_SIZE;
    			 
    			 Attribute used = new Attribute(name, aID, rID, type, nullable, distinct, length);
//    			 System.out.println("" + used.getType());
    			 attributes.add(used);
    			 System.out.println("Loading attribute " + name 
    					 + " to relation " 
    					 + rID);
    			 relationHolder.getRelation(rID).addAttribute(used);
    			 
    		 }
    		 attposition = 0;
    	 }
//    	 System.out.println("Finished Loading");
    }
    
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
    public boolean createIndex(String relation, String attribute,
    		String Indexname, boolean duplicates) {
        long rID = -1, aID = -1;
    	int i, j;
    	Attribute.Type aType = Attribute.Type.Undeclared;
    	Relation rel =  null;
    	Attribute att = null;
    	Iterator it = null;
    	BTree b = new BTree();
    	
//    	System.out.println("This had better get called");
    	
    	StorageManager.openFile("" + Indexname + ".if");
    	
    	int idx;
        ArrayList <Relation> relations = relationHolder.getRelations();
        //Find the rID and aID for use with the indexing.
    	for (i = 0; i < relations.size(); i++) {
        	if (relations.get(i).getName().equalsIgnoreCase(relation)) {
        		rID = relations.get(i).getID();
        		rel = relations.get(i);
        		break;
        	}
        }
        
        for (j = 0; j < attributes.size(); j++) {
        	if (attributes.get(j).getName().equalsIgnoreCase(attribute) && attributes.get(j).getParent() == rID) {
        		aID = attributes.get(j).getID();
        		aType = attributes.get(j).getType();
        		att = attributes.get(j);
        		break;
        	}
        }
        
//        System.out.println(relation);
        
        //if it works open the appropriate type of index, duplicate or not
        if (rel == null || rel.containsIndex(""+Indexname)){
        	return false;
        } else if (aType!=Attribute.Type.Int && aType!=Attribute.Type.Long){
        	return false;
        } else{
	        if (duplicates) {
	        	rel.addIndex(Indexname);
	        	att.setIndex(Indexname.toCharArray());
	        	idx = b.OpenIndex(""+Indexname+".if", true);
	        	it = relationHolder.getRelation(rID).open(); 
        	} else {
        		rel.addIndex(Indexname);
            	att.setIndexd(Indexname.toCharArray());
            	idx = b.OpenIndex(""+Indexname+".if", false);
            	it = relationHolder.getRelation(rID).open();
        	}
        	
        }
        
        System.out.println("INSERTING AN INDEX VAL");
        
        //Iterate through the Relation and insert the values into the index.
        String[] record;
        String keystr;
        long key;
        long address;
        while(it.hasNext()) {
        	
        	record = it.getNext();
        	keystr = record[rel.indexOfAttribute(aID)];
        	key = Long.parseLong(keystr);
        	address = it.getAddress();
        	b.Insert(idx, key, address);
        	
        }
        b.CloseIndex(idx);
        it.close();
        
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
    		
    		
    		//Add the entry fo this attribute to the catalog. 
    		//Determine which block this thing belongs in.
    		int blockNumber = (int) newAttribute.getID() 
    			/ (StorageManager.BLOCK_SIZE / ATT_REC_SIZE);
    		//How many bytes into the block is this attribute?
    		int offset = ((int) newAttribute.getID() 
    			% (StorageManager.BLOCK_SIZE / ATT_REC_SIZE)) * ATT_REC_SIZE;
    		/*System.out.println("Adding attribute " + attributeName 
    				+ " to the attribute catalog at block " + blockNumber
    				+ " and byte " + offset + "...");*/
    		//Now get the entry for this biotch in the attribute catalog
    		ByteBuffer entry = newAttribute.writeCrapToBuffer();
    		//Get the block that this new attribute should be recorded in
    		ByteBuffer block = buffer.readAttributeCatalog(
    			SELECT_ATTRIBUTE_CATALOG, blockNumber);
    		//Put the new attribute at the correct place in the block
    		byte [] array = entry.array();
    		for (int index = 0; index < array.length; index++) {
    			block.put(index + offset, array[index]);
    		}
    	}
    	
    	//System.out.println(relationHolder);
        return true;
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
    
    private String parseComparison(String condition) {
    	String tail = condition.split("\\s")[3];
    	System.out.println(tail);
    	return (tail.split("\\]"))[0];
    }
    
    private String parseConditionAttribute(final String condition) {
    	//TODO allow it to handle more than the word after the where.
    	//At this point it should be the second word
//    	System.out.println(condition);
    	return condition.split("\\s")[1]; 	
    }
    
    private String [] parseSelectAttributes(final String selection) {
    	//Split up the selection statement
    	String [] commands = selection.split("\\s");
    	//And the return array
    	String attributes = "";
    	
    	//Start on index 1 because that's where the attributes start
    	for (int word = 1; word < commands.length; word++) {
    		if (commands[word].equalsIgnoreCase("FROM")) {
    			break;
    		} else { 
    			attributes = attributes + " " + commands[word];
    		}
    	}
    	return attributes.split("\\s");
    }
    
    private String parseSelectTable(final String selection) {
    	//Fist thing to do is to find the relation that this thing works on.
    	String [] commands = selection.split("\\s");
    	//Find the workd "TABLE" and we know the word after that
    	//is the relation.
    	for (int index = 0; index < commands.length; index++) {
    		if (commands[index].equalsIgnoreCase("TABLE")) {
    			return commands[index + 1];
    		}
    	}
    	return null;
    }
    
    private String parseWhereClause(final String selection) {
    	//Find the where statement.
    	String [] commands = selection.split("\\s");
    	String whereClause = "";
    	
    	for (int index = 0; index < commands.length; index++) {
    		if (commands[index].equalsIgnoreCase("[WHERE")) {
    			for (int word = index; word < commands.length; word++) {
    				whereClause += commands[word] + " ";
    			}
    		}
    	}
    	return whereClause;
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
    		 block = buffer.readAttributeCatalog(attributecatalog, attributemarker
    				 * StorageManager.BLOCK_SIZE + ATT_OFFSET);
    		 //System.out.println("Error Isn't Here Either.");
    		 
    		 //Loop through the entirety of this attribute block parsing things
    		 for (int j = 0; j < StorageManager.BLOCK_SIZE / ATT_REC_SIZE; j++) {
    			 
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
    			 String data = name + aID + nullable + type + rID + distinct
    			 	+ length;
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
    		 block = buffer.readRelationCatalog(relationcatalog, relationmarker
    				 * StorageManager.BLOCK_SIZE + REL_OFFSET);
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
    			 
    			 //Then we need to get the indexed columns
    			 
    			 //Now pack all of the attribute metadata into a string
    			 String data = name + ID + creationDate + modifyDate + records
    			 	+ blocktotal;
    			 
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
    	String variable = parseComparison(whereClause);
    	//System.out.println(variable);
    	
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
			System.out.println("Comparing " + value + " and "
    				+ variable);
    		if (value.equalsIgnoreCase(variable)) {
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
		long blockNumber = rID / recordsPerBlock;
		int recordInBlock = rID % recordsPerBlock;
		int offSet = recordInBlock * REL_REC_SIZE;
		
		//Get the block to write this relations metadata to from the buffer
		ByteBuffer block = buffer.readRelationCatalog(
				relationcatalog, blockNumber);

		//Now actually write the relations data into the block
		byte [] array = entry.array();
		for (int index = 0; index < array.length; index++) {
			block.put(index + offSet, array[index]);
		}
		
		buffer.writeRelCatalog(relationcatalog, blockNumber, block);
	}
	
	
    
    public static void main(String[] args){
    	SystemCatalog sc = new SystemCatalog();

    	//Make a whole mess of tables
    	int tables = 9;
    	String tableNumber = "TABLE_NUMBER_";
    	for (int table = 1; table <= tables; table++) {
        	sc.createTable("CREATE TABLE " + tableNumber + table 
        		+ " (CHAR" + table + " CHAR 5)", "key");
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
    	}
    	
    	sc.buffer.flush();
    	
    	for (int table = 2; table <= tables; table++) {
    		String select = "SELECT * FROM TABLE " + tableNumber + table;
    		select = select + " [WHERE CHAR" + table + " = ";
    		select = select + " " + 1 * table + "]";
    		System.out.println(select);
    		String [] result = sc.selectFromTable(select);
    		for (int index = 0; index < result.length; index++) {
    			System.out.print(result[index]);
    		}
    		System.out.println();
    	}
    }
}
