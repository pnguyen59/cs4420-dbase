package queries;

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
	}
