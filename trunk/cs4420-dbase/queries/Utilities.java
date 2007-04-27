package queries;

public class Utilities {
	public static String printArray(String[] vals){
		String ret = "{";
		for (int j=0; j<vals.length; j++){
			ret+=vals[j]+", ";
		}
		ret = ret.substring(0, ret.length()-2)+"}";
		return ret;
	}
	
	public static String getProperAttName(String var){
		String[] vals = var.replace("(", "").replace(")", "").split(" ");
		if (vals[0].equalsIgnoreCase("qa")){
			return vals[1].replace("\"", "")+"."+vals[2].replace("\"", "");
		} else if (vals[0].equalsIgnoreCase("a")){
			return vals[1].replace("\"", "");
		} else {
			return vals[0];
		}
	}
	}
