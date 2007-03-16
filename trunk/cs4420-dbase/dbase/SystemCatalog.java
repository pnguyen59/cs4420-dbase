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
    
    /**The Database's buffer */
    public BufferManager buffer;
    
    /**String Length Maximum*/
    public final static int stringlength = 15;
    
    /**A list of Relations and Attributes */
    private ArrayList <Attribute> attributes;
    
    /** FileChannels for the attribute and relation catalogs*/
    private FileChannel relationcatalog = StorageManager.openFile("RELATION_CATALOG.rc");
    private FileChannel attributecatalog = StorageManager.openFile("ATTRIBUTE_CATALOG.ac");
    long relationmarker = 0, attributemarker = 0;
    public static final int ATT_REC_SIZE = 73;
    public static final int REL_REC_SIZE = 398;
    public final static long ATT_OFFSET = (long)Math.pow(2, 31);
    public final static long REL_OFFSET = (long)Math.pow(2, 30);
    
    /** Singleton relationholder */
    private RelationHolder relationHolder = RelationHolder.getRelationHolder();
    
    /** Creates a new instance of SystemCatalog */
    public SystemCatalog() {
    	attributes = new ArrayList<Attribute>();
    	buffer = BufferManager.getBufferManager();
    	int relsize = 0, attsize = 0;
    	
    	try {
    		relsize = (int) (relationcatalog.size() / (int) StorageManager.BLOCK_SIZE);
    		attsize = (int) (attributecatalog.size() / (int) StorageManager.BLOCK_SIZE);
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
    		for(int j = 0; j < StorageManager.BLOCK_SIZE / REL_REC_SIZE; j++) {
    			if (relbuffer.getChar(relationpos) == BufferManager.NULL_CHARACTER) {
    				break;
    			}
    			for (int k = 0; k < 15; k++) {
    				if (relbuffer.getChar(relationpos) != BufferManager.NULL_CHARACTER) {
    					relname += relbuffer.getChar(relationpos);
    				}
    				relationpos += Attribute.CHAR_SIZE;
    			}
    			ID = relbuffer.getInt(relationpos);
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
    		 attbuffer = buffer.readRel(attributecatalog, attributemarker * StorageManager.BLOCK_SIZE + ATT_OFFSET);
    		 for (int j = 0; j < StorageManager.BLOCK_SIZE / ATT_REC_SIZE; j++) {
    			 if (attbuffer.getChar(attposition) == BufferManager.NULL_CHARACTER) {
    				 break;
    			 }
    			 String name = "";
    			 for (int k = 0; k < 15; k++) {
    				 if (attbuffer.getChar( attposition) != BufferManager.NULL_CHARACTER) {
    					 name += attbuffer.getChar(attposition);
    					 attposition += Attribute.CHAR_SIZE;
    				 }
    			 }
    			 
    		 }
    	 }
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
    	relationname = relation.substring(relation.indexOf(" ", relation.toLowerCase().indexOf("table"))+1, relation.indexOf("("));
    	Relation rel = new Relation(relationname, relationHolder.getSmallestUnusedID());
    	relationHolder.addRelation(rel);
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
    		}
    		Attribute att;
    		if (type == Attribute.Type.Char){
    			 att = rel.addAttribute(attributename, type, getSmallestUnusedAttributeID(), size);
    		} else {
    			att = rel.addAttribute(attributename, type, getSmallestUnusedAttributeID());
    		}
    		attributes.add(att);
    	}
    	
    	
    	System.out.println(relationHolder);
    	
    	
        return true;
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
    	
    	System.out.println("This had better get called");
    	
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
        
        System.out.println(relation);
        
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
     *Returns an Iterator on the given relation.
     *
     *@param relation the relation to be used
     *
     *@return The Iterator
     */
//    public Iterator getIterator(String relation) {
//    	//TODO Implement and uncomment out
//        return null;
//    }
    
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
    
    /**
     *Returns the rows from a Table
     *@param selection the code from the user
     *@return The Table in String format.
     */
    public String [] selectFromTable(String selection) {

    	//Parse out the desired data fields from the select
    	String [] attributes = parseSelectAttributes(selection);
    	for (int i = 0; i < attributes.length; i++) {
    		System.out.println(attributes[i]);
    	}
    	
    	//Parse out the desired table to work on and various parts of the
    	//Select statement
    	String table = parseSelectTable(selection);
    	System.out.println(table);
    	String whereClause = parseWhereClause(selection);
    	System.out.println(whereClause);
    	String conditionAttribute = parseConditionAttribute(whereClause);
    	System.out.println(conditionAttribute);
    	//Get the variable, what we are comparing the attribute against
    	String variable = parseComparison(whereClause);
    	System.out.println(variable);
    	
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
    		if (values[attributeIndex].equalsIgnoreCase(variable)) {
    	    	for (int i = 0; i < values.length; i++) {
    	    		System.out.print(values[i]);
    	    	}
    	    	System.out.print("");
    		} 		
    	}
    	
        return null;
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
    	String [] commands = selection.split("\\[");
    	
    	for (int index = 0; index < commands.length; index++) {
    		String [] splitCommand = commands[index].split("\\s");
    		for (int word = 0; word < splitCommand.length; word++) {
    			if (splitCommand[word].equalsIgnoreCase("Where")) {
    				return commands[index];
    			}
    		}
    	}
    	return null;
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
    
    private String parseConditionAttribute(final String condition) {
    	//TODO allow it to handle more than the word after the where.
    	//At this point it should be the second word
    	return condition.split("\\s")[1]; 	
    }
    
    private String parseComparison(String condition) {
    	return (condition.split("\\s"))[4];
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
     *Returns the relation info
     *
     *@param selection the code from the user
     *
     *@return The Relation in String format.
     */
    public String [] selectFromCatalog(String selection) {
    	//TODO Implement Printout.
        return null;
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
	
    
    public static void main(String[] args){
    	SystemCatalog sc = new SystemCatalog();
    	RelationHolder holder = RelationHolder.getRelationHolder(); 
    	sc.createTable("CREATE TABLE table_name(anint int)", "key");
    	sc.createTable("CREATE TABLE t(anint int, achar char 10, achar2 char 20)", "key");
    	sc.insert("INSERT INTO t (achar2, achar, anint) VALUES(a1, abcdefg, 10)");
    	sc.createIndex("CREATE INDEX bob ON t (anint)");
    	sc.insert("INSERT INTO t (achar2, achar) VALUES(a1, abcdefh)");
    	sc.insert("INSERT INTO t (achar2, achar) VALUES(a1, abcdefi)");
    	sc.insert("INSERT INTO t (achar2, achar) VALUES(a1, abcdefj)");
    	sc.insert("INSERT INTO t (achar2, achar) VALUES(a1, abcdefk)");
    	//System.out.println(RelationHolder.getRelationHolder());
    	Relation r = RelationHolder.getRelationHolder().getRelation(1);
    	/*Iterator it = r.open();
    	while (it.hasNext()){
    		String[] r2 = it.getNext();
    		System.out.println(r2[1]);
    	}*/
    	//sc.selectFromTable("SELECT FROM TABLE t WHERE achar = abcdefg");
    }
}
