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
     * @param command The command to parse and possible sent off somewhere.
     * @return Whether or not the query could be parsed correctly.
     */
    public boolean parseCommand(final String command) {
    	//Split the command into an array of strings
    	String [] splitCommand = command.split("/\\s+/");
    	
    	//TODO send the result to SystemCatalog
    	if (splitCommand[0].compareToIgnoreCase("SELECT") == 1) {
    		
    	} else if (splitCommand[0].compareToIgnoreCase("SELECT") == 1) {
    		
    	} else if (splitCommand[0].compareToIgnoreCase("INSERT") == 1) {
    		
    	}
    	
        return true;
    }
    
    /**The main function for the whole dealie.
     * @param ARGVS  Any parameters passed from the command line.
     */
    public static void main(final String [] ARGVS) {
    	
    }
    
}
