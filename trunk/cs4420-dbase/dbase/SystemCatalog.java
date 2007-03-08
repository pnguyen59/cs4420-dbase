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
public class SystemCatalog {
    
    /**The Database's buffer */
    public BufferManager buffer;
    
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
     *@param the relation to be indexed
     *
     *@return whether the index was created successfully
     */
    public boolean createIndex(String relation) {
        return true;
    }
    
    /**
     *returns an Iterato on the given relation.
     *
     *@param relation the relation to be used
     *
     *@return The Iterator
     */
    public Iterator getIterator(String relation) {
        return null;
    }
    
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
