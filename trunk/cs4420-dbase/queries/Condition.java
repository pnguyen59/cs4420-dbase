package queries;

import java.util.ArrayList;

public abstract class Condition {

	/**This method will return a new subclass of the Condition class, depending
	 * on what is passed to it.
	 * @param newCondition The literal represenation of the new Condition.
	 * @param relationIDs The relations involved in the condition.
	 * @return The new Condition.
	 */
	public static Condition makeCondition(final String newCondition) {
		
		
		//No spaces
		String filteredCondition = newCondition.replace(" ", "");
		
		//If there is a "WHERE", destroy it and get rid of the surrounding ()
		if (newCondition.contains(QueryParser.WHERE)) {
			//Remove the where
			filteredCondition = 
				filteredCondition.replace(QueryParser.WHERE, "");
			//Find the first and last index of the parens, and destroy them
			int openIndex = filteredCondition.indexOf("(");
			int closeIndex = filteredCondition.lastIndexOf(")");
			filteredCondition 
				= filteredCondition.substring(openIndex + 1, closeIndex);
		}
		
		//See what kind of condition it is
		String upperCase = filteredCondition.toUpperCase();
		String noSpaces = upperCase.replace(" ", "");
		String noParens = noSpaces.replace("(", "");
		char firstCharacter = noParens.charAt(0);
		
		if (firstCharacter == 'A') {
			return new AndOrCondition(filteredCondition);
		} else if (firstCharacter == 'O') {
			return new AndOrCondition(filteredCondition);
		} else {
			return new SimpleCondition(filteredCondition);
		}
	}
	
	/**The string representing the type of the comparison, and, or, >, etc.*/
	protected String comparison;
	
	/**The string representation of the Condition, i.e. what was typed in.*/
	protected String condition;
	
	/**The list of Relations referenced in this Condition.*/
	protected int relation;
	
	/**This will create a new instance of Condition and initialize the lists
	 * contained in the Condition.
	 */
	public Condition() {
		relation = 0;
	}
	
	/**This will create a new instance of Condition, and initialize the field
	 * "condition" with the value passed.
	 * @param newCondition The value for condition in the new Condition object.
	 */
	public Condition(final String newCondition) {
		this();
		this.condition = newCondition;
	}
	
	/**This will create a new instance of Condition, and initialize the field
	 * "condition" with the value passed.
	 * @param newCondition The value for condition in the new Condition object.
	 * @param newRelation The ID of the Relation this condition works on.
	 */
	public Condition(final String newCondition, final int newRelation) {
		this(newCondition);
		this.condition = newCondition;
		setRelation(relation);
	}
	
	/**The absract compare function, i.e. tell whether or not this Condition is
	 * true.
	 * @param tuple The tuple to look at.
	 * @return Whether or not the comparison is true.
	 */
	public abstract boolean compare(final String[] tupleattnames, final String[] tuplevals);
	
	/**This method will return the list of Attributes used in this Condition,
	 * and any contained within it.
	 * @return A list of Attributes used in the Condition.
	 */
	public abstract ArrayList < String > getAttributes();
	
	/**This method will return the comparison used in this Conditon.
	 * @return The comparison being used.
	 */
	public String getComparison() {
		return this.comparison;
	}
	
	/**This method will return the literal representation of this Condition.
	 * @return The literal representation of this Condition.
	 */
	public String getCondition() {
		return this.condition;
	}
	
	/**This method will return the Relations referenced in this 
	 * Condition and any contained within it.
	 * @return A the Relation used in this Condition. 
	 */
	public int getRelation() {
		return this.relation;
	}
	
	/**This method will set the comparison used in this Condition.
	 * @param newComparison The new comparison to make.
	 */
	public void setComparison(final String newComparison) {
		this.comparison = newComparison;
	}
	
	/**This method will set the literal representation of the comparison.  Does
	 * very little as it won't actually change anything else.
	 * @param newCondition The new literal of this Condition.
	 */
	public void setCondition(final String newCondition) {
		this.condition = newCondition;
	}
	
	/**This method will set the Relation that this Condition 
	 * references.
	 * @param relationID The new Relation.
	 */
	public void setRelation(final int relationID) {
		this.relation = relationID;
	}
	
	public String toString() {
		return condition;
	}
}

