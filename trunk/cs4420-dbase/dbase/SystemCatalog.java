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

import java.util.*;
public class SystemCatalog {
    
    /**The Database's buffer */
    public BufferManager buffer;
    
    /**A list of Relations and Attributes */
    private ArrayList<Relation> relations;
    private ArrayList<Attribute> attributes;
    
    /** Creates a new instance of SystemCatalog */
    public SystemCatalog() {
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
        
        //Find the rID and aID for use with the indexing.
    	for (i = 0; i < relations.size(); i++) {
        	if (relations.get(i).getFilename().equalsIgnoreCase(relation)) {
        		rID = relations.get(i).getID();
        		break;
        	}
        }
        
        for (j = 0; j < attributes.size(); j++) {
        	if (attributes.get(j).getName().equalsIgnoreCase(attribute) && attributes.get(j).getParent() == rID) {
        		aID = attributes.get(j).getID();
        	}
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
        return null;
    }
    
    
}
