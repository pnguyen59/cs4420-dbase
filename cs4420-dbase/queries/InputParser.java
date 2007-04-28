package queries;

import java.util.Scanner;

import dbase.Database;

public class InputParser {
	
	public static void main (String[] args){
		String input =  "";
    	Database db = new Database();
    	while (!input.toLowerCase().trim().equals("exit")){
    		Scanner sc = new Scanner(System.in).useDelimiter("\n");
    		input = sc.next();
    		System.out.println(input);
    		if (!input.toLowerCase().trim().equals("exit")) {
    			System.out.println(db.parseCommandNew(input.replace("\"", "")));
    			System.exit(1);
    		}
    		
    		//Then see what kind of query it is
    		
    	}
    }
	}
	
	

