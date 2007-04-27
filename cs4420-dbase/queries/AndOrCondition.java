package queries;

import java.util.ArrayList;

/**
 * AND/OR condition, fo rizzle
 * @author SomeDude
 *
 */public class AndOrCondition extends Condition {

	/**This method will parse out the two conditions inside of an AND or an 
	 * OR statement.
	 * @param statement The statement to parse.
	 * @return An array containing the inner statements
	 */
	public static String [] parseConditons(String statement) {
		
		String [] conditions = new String [2];
		ArrayList < String > parts = new ArrayList < String > ();
		
		int opens = 0;
		int closes = 0;
		statement = statement.trim();
		//Start at position 1 because we know that 0 is a "("
		for (int start = 1; start < statement.length(); start++) {
			
			//See if it is an open parentheses
			if (statement.charAt(start) == '(') {
				
				opens++;
				
				//Find the close of the condition
				for (int close = start + 1; 
					close < statement.length(); close++ ) {
					
					//See if it is an open parentheses
					if (statement.charAt(close) == '(') {
						opens++;
					} else if (statement.charAt(close) == ')') {
						closes++;
					}
					//See if it is closed now 
					if (closes == opens) {
						parts.add(statement.substring(start, close + 1));
						//System.out.println(statement.substring(start, close + 1));
						//Now go on from here
						start = close;
						break;
					}
				} //End loop looking of )
			} //End if for (
		} //End loop for looking for (
		
		conditions[0] = parts.get(0);
		conditions[1] = parts.get(1);
		return conditions;
	}
	
	/**
	 * parses the left-hand side of an AND/OR statement
	 * @param statement the statement this parses
	 * @return the left hand side of the statement
	 */public static String parseLeftHand(final String statement) {
		return parseConditons(statement)[0];
	}
	
	/**This method finds the right hand side of the AND/OR statement passed 
	 * to it.
	 * @param statement The AND/OR statement.
	 * @return The right hand of the statement.
	 */
	public static String parseRightHand(final String statement) {
		return parseConditons(statement)[1];
	}
	
	private Condition leftHand;
	
	private Condition rightHand;
	
	
	/**This will create a new instance of AndOrCondition.
	 * @param newCondition The condition that the object will represent and
	 * parse sub conditions from.
	 * @param relationID The ID of the relation that this condition will be 
	 * working on, for schema purposes.
	 */
	public AndOrCondition(final String newCondition) {
		super(newCondition);
		//Get the conditions contained within this one.
		System.out.println("LC: "+newCondition);
		if (newCondition.replace("(","").toLowerCase().startsWith(QueryParser.AND.toLowerCase())){
			this.comparison = QueryParser.AND;
		} else if (newCondition.replace("(","").toLowerCase().startsWith(QueryParser.OR.toLowerCase())){
			this.comparison = QueryParser.OR;
		} 
		leftHand = Condition.makeCondition(parseLeftHand(newCondition));
		rightHand = Condition.makeCondition(parseRightHand(newCondition));
	}
	
	/**This method determines whether or not the AndOrCondition is true for the 
	 * tuple passed to it.  The tuple had <b>BETTER</b> be of the same type of
	 * relation as this object thinks it is.
	 * @param tuple The string representation of the tuple.
	 * @return Wether or not this Condition is true for the tupe.
	 */
	public boolean compare(final String[] tupleattnames, final String[] tuplevals, final String[] tupletypes) {
	
		System.out.println("C: "+leftHand.getComparison());
		boolean leftHandEval = leftHand.compare(tupleattnames, tuplevals, tupletypes);
		
		boolean rightHandEval = rightHand.compare(tupleattnames, tuplevals, tupletypes);
		
		//If an AND then see if both the left and right are true
		if (comparison.equalsIgnoreCase("AND")) {
			return (leftHandEval && rightHandEval);
		} else if (comparison.equalsIgnoreCase("OR")) {
			return (leftHandEval || rightHandEval);
		} else {
			System.out.println("AndOrCondition.compare: Not an AND or an OR");
			return false;
		}
	}

	@Override
	public ArrayList < String > getAttributes() {

		//Get the leftHand ones
		ArrayList < String > leftHandAttributes = leftHand.getAttributes();
		ArrayList < String > rightHandAttributes = rightHand.getAttributes();
		ArrayList < String > noDuplicates = 
			(ArrayList) leftHandAttributes.clone();
		
		//See if there are any on the right side not in the left side
		for (int right = 0; right < rightHandAttributes.size(); right++) {
			
			String rightAttribute = rightHandAttributes.get(right);
			boolean duplicate = false;
			
			for (int left = 0; left < leftHandAttributes.size(); left++) {
				
				String leftAttribute = leftHandAttributes.get(left);
				if (leftAttribute.equals(rightAttribute)) {
					duplicate = true;
					break;
				}	
			}
			
			if (!duplicate) {
				noDuplicates.add(rightAttribute);
			}
		}
		return noDuplicates;

	}
	
	/**This method will return the list of conditions inside of this dealie.
	 * It should only return SimpleConditions
	 * @return The SimpleConditions inside of this AndOrCondition
	 */
	public ArrayList < SimpleCondition > getConditions() {
		
		//If one side or the other is an AND or an OR, then ask it for its 
		//conditions
		ArrayList < SimpleCondition > conditions =
			new ArrayList < SimpleCondition > ();
		
		//Get the conditions from the right hand
		ArrayList < SimpleCondition > sub = leftHand.getConditions();
		for (int index = 0; index < sub.size(); index++) {
			conditions.add(sub.get(index));
		}
		
		//Get the conditions from the left hand
		sub = rightHand.getConditions();
		for (int index = 0; index < sub.size(); index++) {
			conditions.add(sub.get(index));
		}
		
		return conditions;		
	}
	
	/**Returns the left side of this AndOrCondition.
	 * @return The left side of this AndOrCondition.
	 */
	public Condition getLeftHand() {
		return this.leftHand;
	}
	
	/**Returns the right side of this AndOrCondition.
	 * @return The right side of this AndOrCondition.
	 */
	public Condition getRightHand() {
		return this.rightHand;
	}
}
