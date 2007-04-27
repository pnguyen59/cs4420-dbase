/*
 * Database.java
 *
 * Created on March 6, 2007, 5:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dbase;

import java.util.Scanner;

/**
 *
 * @author andrewco
 */
public class Database {
    
	private static SystemCatalog catalog;
	
    /** Creates a new instance of Database. */
    public Database() {
    	catalog = new SystemCatalog();
    }
    
    /**This will create a new database if one isn't specified by the user.
     */
    public void initialize() {
        
    }
    
    public static SystemCatalog getCatalog(){
    	return catalog;
    }
    
    /**This will look at a command to see if it is valid.  This will be moved
     * to QueryParser in phase 2 of the project.
     * @param command The command to parse and possible sent off somewhere.
     * @return Whether or not the query could be parsed correctly.
     */
    public String parseCommand(final String command) {
    	//Split the command into an array of strings
    	String [] splitCommand = command.split(" ");
    	if (splitCommand[0].compareToIgnoreCase("SELECT") == 0) {
    		if (splitCommand[3].toLowerCase().compareToIgnoreCase("TABLE")== 0){
    			String ret = "";
    			String[] str = catalog.selectFromTable(command);
    			for (int j=0; j<str.length; j++){
    				ret+=str[j]+"\n";
    			}
    			return ret;
    		} else if (splitCommand[3].toLowerCase().compareToIgnoreCase("INDEX")== 0){
    			return catalog.selectFromIndex(command).toString();
    		}  else if (splitCommand[3].toLowerCase().compareToIgnoreCase("CATALOG")== 0){
    			return catalog.selectFromCatalog(command).toString();
    		} else {
    			return "Command not recognized";
    		}
    	} else if (splitCommand[0].compareToIgnoreCase("CREATE") == 0) {
    		if (splitCommand[1].compareToIgnoreCase("TABLE") == 0){
    			if( catalog.createTable(command, "key")){
    				return "Table Successfully Created";
    			} else {
    				return "Error Creating Table";
    			}
    		} else if (splitCommand[1].compareToIgnoreCase("INDEX") == 0){
    			if (catalog.createIndex(command)){
    				return "Index Successfully Created";
    			} else {
    				return "Error Creating Index";
    			}
    			
    		} else {
    			return "Command not Recognized";
    		}
    		
    	} else if (splitCommand[0].compareToIgnoreCase("INSERT") == 0) {
    		if (catalog.insert(command)){
    			return "Record successfully inserted";
    		} else {
    			return "Error inserting record";
    		}
    	}
    	
        return "Command not Recognized";
    }
    
    /**The main function for the whole dealie.
     * @param ARGVS  Any parameters passed from the command line.
     */
    public static void main(final String [] ARGVS) {
    	String input =  "";
    	Database db = new Database();
    	while (!input.toLowerCase().trim().equals("exit")){
    		Scanner sc = new Scanner(System.in).useDelimiter("\n");
    		input = sc.next();
    		System.out.println(input);
    		if (!input.toLowerCase().trim().equals("exit"))System.out.println(db.parseCommand(input));
    		
    	}
    	System.exit(1);
    }
    
}
