package queries;

import java.util.ArrayList;

import dbase.Attribute;
import dbase.Relation;
import dbase.Attribute.Type;

/**
 * This class is for stuff used anywhere that doesn't really have a proper home
 * @author dkitch
 *
 */
public class Utilities {
	/**
	 * Couldn't be bothered to override String[]'s toString so I made this instead
	 * @param vals the String[] to be printed all pretty
	 * @return the pretty string representation
	 */
	public static String printArray(String[] vals){
		String ret = "{";
		for (int j=0; j<vals.length; j++){
			ret+=vals[j]+", ";
		}
		ret = ret.substring(0, ret.length()-2)+"}";
		return ret;
	}
	
	/**
	 * Turns (qa Relation Attr) into Relation.Attr format, and (a Attr) into Attr format
	 * @param attr the crappily-formatted attribute
	 * @return the good representation
	 */
	public static String getProperAttName(String attr){
		String[] vals = attr.replace("(", "").replace(")", "").split(" ");
		if (vals[0].equalsIgnoreCase("qa")){
			return vals[1].replace("\"", "")+"."+vals[2].replace("\"", "");
		} else if (vals[0].equalsIgnoreCase("a")){
			return vals[1].replace("\"", "");
		} else {
			return vals[0];
		}
	
	
	}
	
	public static Type findAttributeType(final String attributeStatement,
		final ArrayList < Relation > relations) {
		
		//Trim and chop off the parens
		String noParens = attributeStatement.replace(")", "");
		noParens = noParens.replace("(", "");
		String trimmed = noParens.trim();
		
		//First see if it is a constant, if so then horray, much easier
		if (trimmed.substring(0,1).equalsIgnoreCase("K")) {
			//Find the next real letter, to determine what type it is
			trimmed = trimmed.replace(" ", "");
			String type = trimmed.substring(1, 2);
			if (type.equalsIgnoreCase("I")) {
				return Type.Int;
			} else if (type.equalsIgnoreCase("C")) {
				return Type.Char;
			} else if (type.equalsIgnoreCase("D")) {
				return Type.DateTime;
			}
		}
		
		//Since it is now not a constant if it is here, we know it is an
		//attribute.  Find the relation that has it and return the type.
		String attributeName = QueryParser.parseAttribute(attributeStatement);
		for (int index = 0; index < relations.size(); index++) {
		
			//Get the relation out and see if it contains the attribute
			Relation currentRelation = relations.get(index);
			Attribute attribute = 
				currentRelation.getAttributeByName(attributeName);
			
			//If it isn't null then return the type
			if (attribute != null) {
				return attribute.getType();
			}
		}
		
		return null;
	}
}
