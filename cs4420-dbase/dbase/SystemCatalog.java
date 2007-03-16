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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;
public class SystemCatalog {
    
    /**The Database's buffer */
    public BufferManager buffer;
    
    /**A list of Relations and Attributes */
    private ArrayList <Relation> relations;
    private ArrayList <Attribute> attributes;
    
    private RelationHolder relationHolder = RelationHolder.getRelationHolder();
    
    /** Creates a new instance of SystemCatalog */
    public SystemCatalog() {
    	attributes = new ArrayList<Attribute>();
    	buffer = BufferManager.getBufferManager();
    	//TODO read in files
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
    		Attribute.Type type;
    		if (attributetype.toLowerCase().equals("int")){
    			type = Attribute.Type.Int;
    		} else if (attributetype.toLowerCase().equals("long")){
    			type = Attribute.Type.Long;
    		} else if (attributetype.toLowerCase().equals("boolean") || attributetype.toLowerCase().equals("bool")){
    			type = Attribute.Type.Boolean;
    		} else if (attributetype.toLowerCase().equals("char") || attributetype.toLowerCase().equals("character")){
    			type = Attribute.Type.Char;
    		} else if (attributetype.toLowerCase().equals("float")){
    			type = Attribute.Type.Float;
    		} else if (attributetype.toLowerCase().equals("double")){
    			type = Attribute.Type.Double;
    		} else {
    			type = Attribute.Type.Undeclared;
    		}
    		
    		Attribute att = rel.addAttribute(attributename, type);
    		attributes.add(att);
    	}
    	
    	System.out.println(relationHolder);
    	
    	
        return true;
    }
    
    /**
     *Creates an index on an existing relation.
     *
     *@param relation the relation to be indexed.
     *@param attribute the attribute to be indexed on.
     *
     *@return whether the index was created successfully
     */
    public boolean createIndex(String relation, String attribute) {
        long rID = -1, aID = -1;
    	int i, j;
    	Attribute.Type aType = Attribute.Type.Undeclared;
    	Relation rel =  null;
        
        //Find the rID and aID for use with the indexing.
    	for (i = 0; i < relations.size(); i++) {
        	if (relations.get(i).getFilename().equalsIgnoreCase(relation)) {
        		rID = relations.get(i).getID();
        		rel = relations.get(i);
        		break;
        	}
        }
        
        for (j = 0; j < attributes.size(); j++) {
        	if (attributes.get(j).getName().equalsIgnoreCase(attribute) && attributes.get(j).getParent() == rID) {
        		aID = attributes.get(j).getID();
        		aType = attributes.get(j).getType();
        	}
        }
        
        if (rel == null || rel.containsIndex("./"+rID+aID+".if")){
        	return false;
        } else if (aType!=Attribute.Type.Int && aType!=Attribute.Type.Long){
        	return false;
        } else{
        	BTree b = new BTree();
        	int idx = b.OpenIndex("./"+rID+aID+".if", true);
        	Iterator it = relationHolder.getRelation(rID).open(); 
        	
        }
        
        aID++; //delete this later, just here to remove warning.
        
        //TODO put in the iteration/insert algorythm here.
        
        return true;
    }
    
    /**
     *returns an Iterato on the given relation.
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
    	//TODO Implement Insert
        return true;
    }
    
    /**
     *Returns the rows from a Table
     *
     *@param selection the code from the user
     *
     *@return The Table in String format.
     */
    public String [] selectFromTable(String selection) {
    	//TODO Implement printout.
        return null;
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
    	relation.addRecord(block, record);

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
    	sc.createTable("CREATE TABLE table_name(anint int)", "key");
    	sc.createTable("CREATE TABLE t(abool boolean, anint int)", "key");
    }
}
