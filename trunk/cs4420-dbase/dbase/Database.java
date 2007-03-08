/*
 * Database.java
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
public class Database {
    
    /** Creates a new instance of Database. */
    public Database() {
    }
    
    /**This will create a new database if one isn't specified by the user.
     */
    public void initialize() {
        
    }
    
    /**This will look at a command to see if it is valid.  This will be moved
     * to QueryParser in phase 2 of the project.
     * @return Whether or not the query could be parsed correctly.
     */
    public boolean parseCommand() {
        return true;
    }
    
}
