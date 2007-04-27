package queries;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class SimpleCondition extends Condition {
	
	/**The left hand side to the simple condition.*/
	private String leftHand;
	
	/**The right hand side to the simple condition.*/
	private String rightHand;
	
	/**
	 * It's a constructor.  WoooHOOOOOOey
	 * @param condition the condition this thingy represents
	 */public SimpleCondition(final String condition) {
		super(condition);
		
		System.out.println(condition);
		
		//See what type of condition this is
		if (condition.toUpperCase().contains(QueryParser.LESS_THAN.toUpperCase())) {
			this.comparison = QueryParser.LESS_THAN;
		} else if (condition.toUpperCase().contains(QueryParser.GREATER_THAN.toUpperCase())) {
			this.comparison = QueryParser.GREATER_THAN;
		} else if (condition.toUpperCase().contains(QueryParser.EQUAL.toUpperCase())) {
			this.comparison = QueryParser.EQUAL;
		}
		
		//Get the right and left hand from this
		ArrayList < String > parts = QueryParser.parseStatementParts(condition);
		setLeftHand(parts.get(0));
		setRightHand(parts.get(1));
	}
	
	@Override
	public boolean compare(final String[] tupleattnames, final String[] tuplevals, final String[] tupletypes) {
		
		String leftval = leftHand.replace("(", "").replace(")", ""); //default: treat them as constants
		String rightval = rightHand.replace("(", "").replace(")", "");
		
		String attname = Utilities.getProperAttName(leftval);
		String lefttype = "int";
		
		
		for (int j=0; j<tupleattnames.length; j++){
			if (attname.equals(tupleattnames[j])){
				leftval = tuplevals[j];
				lefttype = tupletypes[j];
			}
		}
		
		String type = lefttype;
		String rightvalprop = "";
		System.out.println(rightval);
		if (rightval.split(" ")[0].equalsIgnoreCase("const") || 
				rightval.split(" ")[0].equalsIgnoreCase("k")){
			type = rightval.split(" ")[1];
			rightvalprop = rightval.split(" ")[2];
			
		}
		else {
			for (int j=0; j<tupleattnames.length; j++){
				if (Utilities.getProperAttName(rightval).equals(tupleattnames[j])){
					rightvalprop = tuplevals[j];
				}
			}
		}
		 
		
			if (type.equalsIgnoreCase("string")){
				if (this.comparison.equals(QueryParser.EQUAL)){
					return (rightvalprop.replace("\"", "").equals(leftval));
				} else if (this.comparison.equals(QueryParser.LESS_THAN)){
					return (rightvalprop.replace("\"", "").compareTo(leftval) > 0);
				} else if (this.comparison.equals(QueryParser.GREATER_THAN)){
					return (rightvalprop.replace("\"", "").compareTo(leftval) > 0);
				}
			} else if (type.equalsIgnoreCase("float")){
				if (this.comparison.equals(QueryParser.EQUAL)){
					return (Float.parseFloat(rightvalprop)==Float.parseFloat(leftval));
				} else if (this.comparison.equals(QueryParser.LESS_THAN)){
					return (Float.parseFloat(leftval)<Float.parseFloat(rightvalprop));
				} else if (this.comparison.equals(QueryParser.GREATER_THAN)){
					return (Float.parseFloat(leftval)>Float.parseFloat(rightvalprop));
				}
			} else if (type.equalsIgnoreCase("double")){
				if (this.comparison.equals(QueryParser.EQUAL)){
					return (Double.parseDouble(rightvalprop)==Double.parseDouble(leftval));
				} else if (this.comparison.equals(QueryParser.LESS_THAN)){
					return (Double.parseDouble(leftval)<Double.parseDouble(rightvalprop));
				} else if (this.comparison.equals(QueryParser.GREATER_THAN)){
					return (Double.parseDouble(leftval)>Double.parseDouble(rightvalprop));
				}
			} else if (type.equalsIgnoreCase("int")){
				if (this.comparison.equals(QueryParser.EQUAL)){
					return (Integer.parseInt(rightvalprop)==Integer.parseInt(leftval));
				} else if (this.comparison.equals(QueryParser.LESS_THAN)){
					return (Integer.parseInt(leftval)<Integer.parseInt(rightvalprop));
				} else if (this.comparison.equals(QueryParser.GREATER_THAN)){
					return (Integer.parseInt(leftval)>Integer.parseInt(rightvalprop));
				}
			} else if (type.equalsIgnoreCase("dateTime")){
				try{
					if (this.comparison.equals(QueryParser.EQUAL)){
						return (DateFormat.getDateInstance().parse(rightvalprop).equals(DateFormat.getDateInstance().parse(leftval)));
					} else if (this.comparison.equals(QueryParser.LESS_THAN)){
						return (DateFormat.getDateInstance().parse(leftval).before(DateFormat.getDateInstance().parse((rightvalprop))));
					} else if (this.comparison.equals(QueryParser.GREATER_THAN)){
						return (DateFormat.getDateInstance().parse(leftval).after(DateFormat.getDateInstance().parse((rightvalprop))));
					}
					} catch (ParseException e){
						return false;
					}
			} else if (type.split("(")[0].equalsIgnoreCase("char")){
				int length = Integer.parseInt(type.split("(")[1].split(")")[0]);
				String equiv = rightvalprop.replace("\"", "");
				if (this.comparison.equals(QueryParser.EQUAL)){
					return leftval.startsWith(equiv.substring(0, Math.min(length, equiv.length())));
				} else if (this.comparison.equals(QueryParser.LESS_THAN)){
					return (leftval.substring(0, Math.min(length, leftval.length())).compareTo(equiv.substring(0, Math.min(length, equiv.length())))<0);
				} else if (this.comparison.equals(QueryParser.GREATER_THAN)){
					return (leftval.substring(0, Math.min(length, leftval.length())).compareTo(equiv.substring(0, Math.min(length, equiv.length())))>0);
				}
				
				
			} 
		
		return false;
	}

	@Override
	public ArrayList < String > getAttributes() {
		// TODO Auto-generated method stub
		ArrayList < String > attributes = new ArrayList < String > ();
		
		String leftHandAttribute = QueryParser.parseAttribute(leftHand);
		if (leftHandAttribute != null) {
			attributes.add(leftHandAttribute);
		}
		
		String rightHandAttribute = QueryParser.parseAttribute(rightHand);
		if (rightHandAttribute != null) {
			attributes.add(rightHandAttribute);
		}
		
		return attributes;
	}

	/**This method returns the value of leftHand.
	 * @return the leftHand
	 */
	public String getLeftHand() {
		return leftHand;
	}

	/**This method returns the value of rightHand.
	 * @return the rightHand
	 */
	public String getRightHand() {
		return rightHand;
	}

	/**This method will set the value of leftHand.
	 * @param newLeftHand The new value of leftHand.
	 */
	public void setLeftHand(final String newLeftHand) {
		this.leftHand = newLeftHand;
	}

	/**This method will set the value of rightHand.
	 * @param newRightHand The new value of rightHand.
	 */
	public void setRightHand(final String newRightHand) {
		this.rightHand = newRightHand;
	}
	
	public ArrayList < SimpleCondition > getConditions() {
		ArrayList < SimpleCondition > c = new ArrayList < SimpleCondition > ();
		c.add(this);
		return c;
	}

}
