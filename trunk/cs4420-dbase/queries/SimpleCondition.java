package queries;

import java.util.ArrayList;

import dbase.Relation;
import dbase.RelationHolder;

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
	public boolean compare(final String tuple) {
		
		//TODO Evaluate the comparison for the tuple passes
		
		//Evaluate each side for the tuple
		
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
