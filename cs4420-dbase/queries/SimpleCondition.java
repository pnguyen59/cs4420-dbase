package queries;

import java.util.ArrayList;

public class SimpleCondition extends Condition {
	
	/**The left hand side to the simple condition.*/
	private String leftHand;
	
	/**The right hand side to the simple condition.*/
	private String rightHand;
	
	public SimpleCondition(final String condition) {
		super(condition);
		
		System.out.println(condition);
		
		//See what type of condition this is
		if (condition.contains(QueryParser.LESS_THAN)) {
			this.comparison = QueryParser.LESS_THAN;
		} else if (condition.contains(QueryParser.GREATER_THAN)) {
			this.comparison = QueryParser.GREATER_THAN;
		} else if (condition.contains(QueryParser.EQUAL)) {
			this.comparison = QueryParser.EQUAL;
		}
		
		//Get the right and left hand from this
		ArrayList < String > parts = QueryParser.parseStatementParts(condition);
		setLeftHand(parts.get(0));
		setRightHand(parts.get(1));
	}
	
	@Override
	public boolean compare(final String[] tupleattnames, final String[] tuplevals) {
		String leftval = leftHand.replace("(", "").replace(")", ""); //default: treat them as constants
		String rightval = rightHand.replace("(", "").replace(")", "");
		for (int j=0; j<tupleattnames.length; j++){
			if (leftHand.replace("(", "").replace(")", "").equals(tupleattnames[j])){
				leftval = tuplevals[j];
			}
			if (rightHand.replace("(", "").replace(")", "").equals(tupleattnames[j])){
				rightval = tuplevals[j];
			}
		}
		try{
			double j = Double.parseDouble(leftval);
			double k = Double.parseDouble(rightval);
			if (this.comparison.equals(QueryParser.EQUAL)){
				return (j==k);
			} else if (this.comparison.equals(QueryParser.LESS_THAN)){
				return (j<k);
			} else if (this.comparison.equals(QueryParser.GREATER_THAN)){
				return (j>k);
			} 
		} catch (NumberFormatException e){
			if (this.comparison.equals(QueryParser.EQUAL)){
				return leftval.equals(rightval);
			} else if (this.comparison.equals(QueryParser.LESS_THAN)){
				return (leftval.compareTo(rightval)<0);
			} else if (this.comparison.equals(QueryParser.GREATER_THAN)){
				return (leftval.compareTo(rightval)>0);
			} 
		}
		return false;
	}

	@Override
	public ArrayList < String > getAttributes() {
		// TODO Auto-generated method stub
		return null;
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

}
