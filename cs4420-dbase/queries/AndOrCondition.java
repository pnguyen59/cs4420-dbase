package queries;

import java.util.ArrayList;

import dbase.Attribute;
import dbase.Relation;

public class AndOrCondition extends Condition {

	public static String [] parseConditons(final String statement) {
		
		String [] conditions = new String [2];
		ArrayList < String > parts = new ArrayList < String > ();
		
		int opens = 0;
		int closes = 0;
		
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
	
	public static String parseLeftHand(final String statement) {
		return parseConditons(statement)[0];
	}
	
	public static String parseRightHand(final String statement) {
		return parseConditons(statement)[1];
	}
	
	private Condition leftHand;
	
	private Condition rightHand;
	
	public AndOrCondition(final String newCondition, 
		final ArrayList < Integer > relationIDs) {
		super(newCondition);
		
		//Set the relation IDs
		setRelations(relationIDs);
		
		//Get the conditions contained within this one.
		leftHand = Condition.makeCondition(
			parseLeftHand(newCondition), relationIDs);
		rightHand = Condition.makeCondition(
			parseRightHand(newCondition), relationIDs);
	}
	
	@Override
	public boolean compare(ArrayList < String > tuples) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList < Integer > getAttributes() {

		//Get the leftHand ones
		ArrayList < Integer > leftHandAttributes = leftHand.getAttributes();
		ArrayList < Integer > rightHandAttributes = rightHand.getAttributes();
		ArrayList < Integer > noDuplicates = 
			(ArrayList) leftHandAttributes.clone();
		
		//See if there are any on the right side not in the left side
		for (int right = 0; right < rightHandAttributes.size(); right++) {
			
			int rightAttribute = rightHandAttributes.get(right);
			boolean duplicate = false;
			
			for (int left = 0; left < leftHandAttributes.size(); left++) {
				
				int leftAttribute = leftHandAttributes.get(left);
				if (leftAttribute == rightAttribute) {
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
}
