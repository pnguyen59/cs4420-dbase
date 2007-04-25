package queries;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class QueryParser {
	
	public static final String AND = "AND";
	
	public static final String CROSSJOIN = "CROSSJOIN";
	
	public static final String GREATER_THAN = "GT";
	
	public static final String LESS_THAN = "LT";
	
	public static final String OR = "OR";
	
	public static final String PROJECT = "PROJECT";
	
	public static final String SELECT = "SELECT";
	
	public static final int SELECT_FROM_INDEX = 0;
	
	public static final int SELECT_WHERE_INDEX = 1;
	
	public static ArrayList < String > parseQueryAttributes(
		final String query) {
		
		ArrayList < String > attributes = new ArrayList < String > ();
		
		String noSpaces = query.replace(" ", "");
		
		//Look for quotes
		for (int start = 0; start < noSpaces.length(); start++) {
			
			char startCharacter = noSpaces.charAt(start);
			
			//See if it is a quote
			if (startCharacter == '\"') {
				//See if there is an a or QA before hand
				if (noSpaces.charAt(start - 2) == 'Q') {
					
				}
			}
			
		}
		
		return attributes;
	}
	
	/**This method will return the different parts that make up a 
	 * statement.
	 * For example if <code>(SELECT (CROSSJOIN A, B) (WHERE (eq (a "a) (a "b))))
	 * </code> is sent, then <code>(CROSSJOIN A, B)</code> and 
	 * <code>(WHERE (eq (a "a) (a "b)))</code> will be sent back.
	 * @param statement The statement to parse.
	 * @return The parts of that statement.
	 */
	public static ArrayList < String > parseStatementParts(
		final String statement) {
		
		ArrayList < String > parts = new ArrayList < String > ();
		
		int opens = 0;
		int closes = 0;
		
		//Start at position 1 because we know that 0 is a "(", or the first
		//letter, either way we can skip it.
		for (int start = 1; start < statement.length(); start++) {
			
			//See if it is an open parentheses
			if (statement.charAt(start) == '(') {
				
				opens++;
				
				//Find the close of the condition
				for (int close = start + 1; 
					close < statement.length(); close++) {
					
					//See if it is an open parentheses
					if (statement.charAt(close) == '(') {
						opens++;
					} else if (statement.charAt(close) == ')') {
						closes++;
					}
					//See if it is closed now 
					if (closes == opens) {
						parts.add(statement.substring(start, close + 1));
						//Now go on from the start of this part
						start = close;
						break;
					}
				} //End loop looking of )
			} //End if for (
		} //End loop for looking for (
	
		return parts;
	}
	
	public static String removeParentheses(final String statement) {
		int openIndex = statement.indexOf("(");
		int closeIndex = statement.lastIndexOf(")");
		return statement.substring(openIndex, closeIndex);
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
	
}
