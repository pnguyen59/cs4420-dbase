package queries;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class QueryParser {
	
	public static final String AND = "AND";
	
	public static final String CROSSJOIN = "CROSSJOIN";
	
	public static final String EQUAL = "EQ";
	
	public static final String GREATER_THAN = "GT";
	
	public static final String LESS_THAN = "LT";
	
	public static final String OR = "OR";
	
	public static final String PROJECT = "PROJECT";
	
	public static final int PROJECT_ATTRIBUTES_INDEX = 0;
	
	public static final int PROJECT_FROM_INDEX = 1;
	
	public static final String SELECT = "SELECT";
	
	public static final int SELECT_FROM_INDEX = 0;
	
	public static final int SELECT_WHERE_INDEX = 1;
	
	public static final String WHERE = "WHERE";

	public static final String TABLEOPERATION = "TABLEOPERATION";

	public static final String JOIN = "JOIN";
	
	/**This method will find the names of all of the attributes in the thingy
	 * passed to it.  Looks for QA and A
	 * @param query The thingy to look for attributes in
	 * @return The names of the attributes.
	 */
	public static ArrayList < String > parseQueryAttributes(
		final String query) {
		
		ArrayList < String > attributes = new ArrayList < String > ();
		
		//First get rid of parentheses
		String noParens = query.replace("(", "");
		noParens = noParens.replace(")", "");
		//Now split the whole thing up by commas
		String [] split = noParens.split(",");
		
		//Go through the list and find each attribute
		for (int index = 0; index < split.length; index++) {
			
			String currentAttribute = split[index];
			currentAttribute = currentAttribute.replace(" ", "");
			String attributeName = "";
			
			//See if it is a Q or an A
			if (currentAttribute.substring(0, 2).equalsIgnoreCase("QA")) {
				String [] qualifiedAttribute = currentAttribute.split("\"");
				attributeName += qualifiedAttribute[1] + ".";
				attributeName += qualifiedAttribute[3];
				
			} else {
				currentAttribute.replace(" ", "");
				String [] qualifiedAttribute = currentAttribute.split("\"");
				attributeName += qualifiedAttribute[1];
			}
		
			attributes.add(attributeName);
		}
		
		
		return attributes;
	}
	
	/**This method will parse the attributes out of a simple condition, such
	 * as (a "A") or (qa "B" "C")
	 * @param The thing to parse the attribute name out of.
	 * @return The attribute name
	 */
	public static String parseAttribute(final String attribute) {
		
		//Remove any parens
		String noParens = attribute.replace(")", "");
		noParens = noParens.replace("(", "");
		noParens = noParens.replace(" ", "");
		
		//Split it up by quotes
		String [] split = noParens.split("\"");
		if (split[0].equalsIgnoreCase("QA")) {
			return split[1] + "." + split[3];
		} else if (split[0].equalsIgnoreCase("A")) {
			return split[1];
		} else {
			return null;
		}
		
	}
	
	/**This method will take in a statement as a string, and look for any 
	 * table names.  Basically anthing surrounded by quotes.
	 * @param statement The statement.
	 * @return The tables in the statement.
	 */
	public static ArrayList < String > parseRelationNames(
		final String statement) {
		ArrayList < String > tableNames = new ArrayList < String > (); 

		//From the statement, find the tables this is joining stuff from
		//Start by looking for quotes
		for (int start = 0; start < statement.length(); start++) {

			//Get the starting character
			char startingChacter = statement.charAt(start);

			//If it is a quote, then find the next one
			if (startingChacter == '\"') {
				for (int end = start + 1; end < statement.length(); end++) {
					//Get the ending character
					char endingCharacter = statement.charAt(start);
					if (endingCharacter == '\"') {
						//If we have found the close then add it to the list
						//And start over at the next space
						tableNames.add(statement.substring(start + 1,
							end + 1));
						start = end + 1;
						break;
					}
				}
			} //End if
		} //End finding tables loop
		
		return tableNames;
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
				
				opens = 1;
				closes = 0;
				
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
