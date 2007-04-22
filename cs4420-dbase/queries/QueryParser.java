package queries;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class QueryParser {
	
	public static String AND = "AND";
	
	public static String OR = "OR";
	
	public ArrayList < String > parseQuery(String lowerCaseQuery) {
		
		ArrayList < String > parts = new ArrayList < String > ();
		
		String noSpaces = lowerCaseQuery.replace(" ", "");
		String query = lowerCaseQuery.toUpperCase();
		
		int opens = 0;
		int closes = 0;
		
		for (int startingIndex = 0; startingIndex < query.length();
			startingIndex++) {
			
			//If there is a open paren, then find its counterpart.
			if (query.charAt(startingIndex) == '(') {
				//Find the close
				for (int secondIndex = startingIndex; 
					secondIndex < query.length(); secondIndex++) {
					if (query.charAt(secondIndex) == '(') {
						opens++;
					}
					if (query.charAt(secondIndex) == ')') {
						closes++;
					}
					if (opens == closes) {
						parts.add(query.substring(startingIndex,
							secondIndex + 1));
						break;
					}
				}
			}	
		}
		
		return parts;	
	}
	
	public String formQueryTree (final String query) {
		
		//Chop the thing up
		ArrayList < String > tokens = parseQuery(query);
		String operation = tokens.get(0);
		operation = operation.toUpperCase();
		String upperCase = query.toUpperCase();
		
		String tableRow = "";
		
		//Now the big thingy
		if (upperCase.contains("PROJECT")) {
			tableRow = "PROJECT\t";
			tableRow += "\t" + tokens.get(1);
			tableRow += "\t" + tokens.get(2);
			tableRow += "\t" + formQueryTree(tokens.get(2));
		} else if (upperCase.contains("SELECT")) {
			tableRow = "\nSELECT\t";
			tableRow += "\t" + tokens.get(1);
			tableRow += "\t" + tokens.get(2);
			tableRow += formQueryTree(tokens.get(2)); 
		} else if (upperCase.contains("WHERE")) {
			tableRow = "\nWHERE\t";
			tableRow += "\t" + tokens.get(1);
			tableRow += formQueryTree(tokens.get(2));	
		} else if (upperCase.contains("CROSSJOIN")) {
			tableRow = "\nCROSSJOIN\t";	
		}
		
		return tableRow;	
	}
	
	public String removeParentheses(final String statement) {
		int openIndex = statement.indexOf("(");
		int closeIndex = statement.lastIndexOf(")");
		return statement.substring(openIndex, closeIndex);
	}
	
}
