package queries;

import java.util.ArrayList;

import dbase.Attribute;
import dbase.Relation;

public abstract class Condition {

	/**This method will return a new subclass of the Condition class, depending
	 * on what is passed to it.
	 * @param newCondition The literal represenation of the new Condition.
	 * @param relationIDs The relations involved in the condition.
	 * @return The new Condition.
	 */
	public static Condition makeCondition(final String newCondition,
		final ArrayList < Integer > relationIDs) {
		
		//See what kind of condition it is
		String upperCase = newCondition.toUpperCase();
		String noSpaces = upperCase.replace(" ", "");
		String noParens = noSpaces.replace("(", "");
		char firstCharacter = noParens.charAt(0);
		
		if (firstCharacter == 'A') {
			return new AndOrCondition(newCondition, relationIDs);
		} else if (firstCharacter == 'O') {
			return new AndOrCondition(newCondition, relationIDs);
		} else {
			return null;
		}
	}
	
	/**The list of Attributes referenced in this Condition.*/
	protected ArrayList < Integer > attributes;
	
	/**The string representing the type of the comparison, and, or, >, etc.*/
	protected String comparison;
	
	/**The string representation of the Condition, i.e. what was typed in.*/
	protected String condition;
	
	/**The list of Relations referenced in this Condition.*/
	protected ArrayList < Integer > relations;
	
	/**This will create a new instance of Condition and initialize the lists
	 * contained in the Condition.
	 */
	public Condition() {
		this.attributes = new ArrayList < Integer > ();
		this.relations = new ArrayList < Integer > ();
	}
	
	/**This will create a new instance of Condition, and initialize the field
	 * "condition" with the value passed.
	 * @param newCondition The value for condition in the new Condition object.
	 */
	public Condition(final String newCondition) {
		this();
		this.condition = newCondition;
	}
	
	/**The absract compare function, i.e. tell whether or not this Condition is
	 * true.
	 * @return Whether or not the comparison is true.
	 */
	public abstract boolean compare(final ArrayList < String > tuples);
	
	/**This method will return the list of Attributes used in this Condition,
	 * and any contained within it.
	 * @return A list of Attributes used in the Condition.
	 */
	public abstract ArrayList < Integer > getAttributes();
	
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
	
	/**This method will return the list of Relations referenced in this 
	 * Condition and any contained within it.
	 * @return A list of Relations used in this Condition. 
	 */
	public ArrayList < Integer > getRelations() {
		return this.relations;
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
	
	/**This method will set the list of Relations that this Condition 
	 * references.
	 * @param relationIDs The new list of Relations
	 */
	public void setRelations(final ArrayList < Integer > relationIDs) {
		this.relations = relationIDs;
	}
}

