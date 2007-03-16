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
    
	public final static String SELECT_ATTRIBUTE_CATALOG = "ATTRIBUTE_CATALOG";
	
	public final static String SELECT_RELATION_CATALOG = "RELATION_CATALOG";
	
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
    	Relation rel;
    	for(int i = 0; i < relsize; i++) {
    		relbuffer = buffer.readRel(relationcatalog, relationmarker * StorageManager.BLOCK_SIZE + REL_OFFSET);
    		System.out.println("Error Isn't Here.");
    		for(int j = 0; j < StorageManager.BLOCK_SIZE / REL_REC_SIZE; j++) {
    			if (relbuffer.getChar(relationpos) == BufferManager.NULL_CHARACTER) {
    				System.out.println("Null character encountered");
    				break;
    			}
    			for (int k = 0; k < 15; k++) {
    				if (relbuffer.getChar(relationpos) != BufferManager.NULL_CHARACTER) {
    					relname += relbuffer.getChar(relationpos);
    				}
    				relationpos += Attribute.CHAR_SIZE;
    			}
    			ID = relbuffer.getInt(relationpos);
    			System.out.println(ID);
    			rel = new Relation(relname, ID);
    			relationHolder.addRelation(rel);
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
    			for(int l = 0; l < 10; l++) {
    				if (relbuffer.getInt(relationpos) != -1){
    					rel.addIndex(relbuffer.getInt(relationpos));
    					relationpos += Attribute.INT_SIZE;
    				}
    			}
    			for (int m = 0; m < 10; m++) {
    				for (int n = 0; n < 15; n++) {
    					if (relbuffer.getChar(relationpos) != BufferManager.NULL_CHARACTER) {
    					index += relbuffer.getChar(relationpos);
    					}
    					relationpos += Attribute.CHAR_SIZE;
    				}
    				rel.addIndex(index);
    				index = new String();
    			}
    			relationpos = 0;
    			relationmarker++;
    		}
    		
    	}
    	ByteBuffer attbuffer;
    	int attposition = 0;
    	 for (int i = 0; i < attsize; i++) {
    		 attbuffer = buffer.readAtt(attributecatalog, attributemarker * StorageManager.BLOCK_SIZE + ATT_OFFSET);
//    		 System.out.println("Error Isn't Here Either.");
    		 for (int j = 0; j < StorageManager.BLOCK_SIZE / ATT_REC_SIZE; j++) {
    			 if (attbuffer.getChar(attposition) == BufferManager.NULL_CHARACTER) {
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
    			 char type = attbuffer.getChar(attposition);
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
    			 System.out.println(rID);
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
    public boolean createIndex(String relation, String attribute, String Indexname, boolean duplicates) {
        long rID = -1, aID = -1;
    	int i, j;
    	Attribute.Type aType = Attribute.Type.Undeclared;
    	Relation rel =  null;
    	Attribute att = null;
    	Iterator it = null;
    	BTree b = new BTree();
    	
//    	System.out.println("This had better get called");
    	
    	StorageManager.openFile(""+Indexname+".if");
    	
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
     *
     *@param relation the name and atributes still in code format
     *@param the attribute to sort by possibly, probably not (may be removed)
     *
     *@return whether the table was created correctly.
     */
    public boolean createTable(String relation, String key) {
    	String relationname;
    	relationname = relation.substring(relation.indexOf(" ", relation.toLowerCase().indexOf("table"))+1, relation.indexOf("(")).trim();
    	Relation rel = new Relation(relationname, relationHolder.getSmallestUnusedID());
    	relationHolder.addRelation(rel);
    	Attribute att;
    	writeoutRel(rel.writeCrapToBuffer(), rel.getID());
    	StringTokenizer st = new StringTokenizer(relation.substring(relation.indexOf("(")+1,relation.indexOf(")")),",");
    	while (st.hasMoreTokens()){
    		String currentattribute = st.nextToken().trim();
    		String attributename = currentattribute.split(" ")[0];
    		String attributetype = currentattribute.split(" ")[1];
    		int size = 0;
    		Attribute.Type type;
    		if (attributetype.toLowerCase().equals("int")){
    			type = Attribute.Type.Int;
    		} else if (attributetype.toLowerCase().equals("long")){
    			type = Attribute.Type.Long;
    		} else if (attributetype.toLowerCase().equals("boolean") || attributetype.toLowerCase().equals("bool")){
    			type = Attribute.Type.Boolean;
    		} else if (attributetype.toLowerCase().equals("char") || attributetype.toLowerCase().equals("character")){
    			type = Attribute.Type.Char;
    			size = Integer.parseInt(currentattribute.split(" ")[2]);
    		} else if (attributetype.toLowerCase().equals("float")){
    			type = Attribute.Type.Float;
    		} else if (attributetype.toLowerCase().equals("double")){
    			type = Attribute.Type.Double;
    		} else if (attributetype.toLowerCase().equals("datetime")){
    			type = Attribute.Type.DateTime;
    		} else {
    			type = Attribute.Type.Undeclared;
    		} if (type == Attribute.Type.Char){
    			 att = rel.addAttribute(attributename, type, getSmallestUnusedAttributeID(), size);
    		} else {
    			att = rel.addAttribute(attributename, type, getSmallestUnusedAttributeID());
    		}
    		attributes.add(att);
    		
    		//Now get the entry for this biotch in the attribute catalog
    		ByteBuffer entry = att.writeCrapToBuffer();
    		//Determine which block this thing belongs in.
    		//How man bytes in is this attribute?
    		int blockNum = (int)att.getID() / (StorageManager.BLOCK_SIZE / ATT_REC_SIZE);
    		int offset = (int)att.getID() % (StorageManager.BLOCK_SIZE / ATT_REC_SIZE);
    		//System.out.println(blockNum);
    		//System.out.println(attributecatalog);
    		
    		
    		byte [] byter = new byte [StorageManager.BLOCK_SIZE];
    		ByteBuffer temp = buffer.readAtt(attributecatalog, blockNum * StorageManager.BLOCK_SIZE + ATT_OFFSET);
    		//temp.put(byter);
    		byte [] temp2 = entry.array();
    		int bytenum  = (int)offset * ATT_REC_SIZE;
    		for (int i = bytenum; i < bytenum + ATT_REC_SIZE; i++) {
    		byter[i] = temp2[i-bytenum];
    		}
    		ByteBuffer block = ByteBuffer.wrap(byter);
    		buffer.writeAttCatalog(attributecatalog, blockNum, block);
    		
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
    	
    	//Then ask relation to insert the record in this block for us
    	if (record.toLowerCase().indexOf("values") < record.indexOf("(")){
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
    		if (commands[index].equalsIgnoreCase("WHERE")) {
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
    		 block = buffer.readAtt(attributecatalog, attributemarker
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
    		 block = buffer.readRel(relationcatalog, relationmarker
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
    public String [] selectFromIndex(String selection) {
    	//TODO Implement Printout.
        return null;
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
    	//System.out.println(table);
    	String whereClause = parseWhereClause(selection);
    	//System.out.println(whereClause);
    	String conditionAttribute = parseConditionAttribute(whereClause);
    	//System.out.println(conditionAttribute);
    	//Get the variable, what we are comparing the attribute against
    	String variable = parseComparison(whereClause);
    	//System.out.println(variable);
    	
    	//Get the relation that this is working on and the index of the
    	//Attribute under scrutiny
    	long relationID = RelationHolder.
    		getRelationHolder().getRelationByName(table);
    	Relation relation = RelationHolder.
    		getRelationHolder().getRelation(relationID);
    	int attributeIndex = relation.getIndexByName(conditionAttribute);
    	
    	//Now see which records of this relation match
    	Iterator iterator = relation.open();
    	
    	while(iterator.hasNext()) {
    		String [] values = iterator.getNext();
    		//Now just see if they are the same
        	//for (int i = 0; i < values.length; i++) {
        		//System.out.println(values[i]);
        	//}
    		
    		if (values[attributeIndex].equalsIgnoreCase(variable)) {
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
	
	private void writeoutRel(ByteBuffer buffer2, int rID) {
		long blocknum = rID / (StorageManager.BLOCK_SIZE / REL_REC_SIZE);
		long recordnum = rID % (StorageManager.BLOCK_SIZE / REL_REC_SIZE);
		byte [] bytes = new byte [StorageManager.BLOCK_SIZE];
		ByteBuffer temp = buffer.readRel(relationcatalog, blocknum * StorageManager.BLOCK_SIZE + REL_OFFSET);
		//temp.put(bytes);
		byte [] temp2 = buffer2.array();
		int bytenum  = (int)recordnum * REL_REC_SIZE;
		for (int i = bytenum; i < bytenum + REL_REC_SIZE; i++) {
		bytes[i] = temp2[i-bytenum];
		}
		ByteBuffer temp3 = ByteBuffer.wrap(bytes);
		buffer.writeRelCatalog(relationcatalog, blocknum, temp3);
	}
	
	
    
//    public static void main(String[] args){
//    	SystemCatalog sc = new SystemCatalog();
//    	RelationHolder holder = RelationHolder.getRelationHolder(); 
//    	sc.createTable("CREATE TABLE table_name(anint int)", "key");
//    	sc.createTable("CREATE TABLE t(anint int, achar char 10, achar2 char 20)", "key");
//    	sc.insert("INSERT INTO t (achar2, achar, anint) VALUES(a1, abcdefg, 10)");
//    	sc.createIndex("CREATE INDEX bob ON t (anint)");
//    	sc.insert("INSERT INTO t (achar2, achar) VALUES(a1, abcdefh)");
//    	sc.insert("INSERT INTO t (achar2, achar) VALUES(a1, abcdefi)");
//    	sc.insert("INSERT INTO t (achar2, achar) VALUES(a2, abcdefj)");
//    	sc.insert("INSERT INTO t (achar2, achar) VALUES(a3, abcdefk)");
//    	//System.out.println(RelationHolder.getRelationHolder());
//    	Relation r = RelationHolder.getRelationHolder().getRelation(1);
//    	Iterator it = r.open();
//    	while (it.hasNext()){
//    		String[] r2 = it.getNext();
////    		System.out.println(r2[1]);
//    	}
//    	String [] results = sc.selectFromTable("SELECT achar2 FROM TABLE t [WHERE achar = abcdefg]");
//    	//System.out.println();
//    	System.out.println("SELECT achar2 FROM TABLE t [WHERE achar = abcdefg]");
//    		for (int i = 0; i < results.length; i++) {
//    			System.out.print(results[i] + "\n");
//    	}
//    	System.out.println();
//    	
//    	results = sc.selectFromTable("SELECT achar FROM TABLE t [WHERE achar2 = a3]");
//    	//System.out.println();
//    	System.out.println("SELECT achar FROM TABLE t [WHERE achar2 = a3]");
//    		for (int i = 0; i < results.length; i++) {
//    			System.out.print(results[i] + "\n");
//    	}
//    	System.out.println();
//    	
//    	results = sc.selectFromTable("SELECT achar2 FROM TABLE t [WHERE achar2 = a5]");
//    	System.out.println("SELECT achar2 FROM TABLE t [WHERE achar2 = a5]");
//    	for (int i = 0; i < results.length; i++) {
//    		System.out.print(results[i] + "\n");
//    	}
//    	System.out.println();
//    	
//    	results = sc.selectFromTable("SELECT achar2 FROM TABLE t [WHERE achar2 = a1]");
//    	System.out.println("SELECT achar2 FROM TABLE t [WHERE achar2 = a1]");
//    	for (int i = 0; i < results.length; i++) {
//    		System.out.print(results[i] + "\n");
//    	}
//    	System.out.println();
//    }
}
