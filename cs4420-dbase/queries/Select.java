package queries;

import java.util.ArrayList;

import dbase.RelationHolder;


public class Select extends Operation {
	
	protected Condition condition;
	
	/**This will create a new instance of Select.  It will use the 
	 * statement passed to it to find out what the condition of the select is
	 * and what it is selecting from.
	 * @param selectStatement The definition of the select.
	 */
	public Select(final String selectStatement) {
		
		setType(QueryParser.SELECT);
		
		//Tet the list of the parts of this select
		ArrayList < String > selectionParts = 
			QueryParser.parseStatementParts(selectStatement);
		
		//Find out what this thing is selecting from
		String sourceStatement = selectionParts.get(
			QueryParser.SELECT_FROM_INDEX);
		setTableOne(Operation.makeOperation(sourceStatement));
		tableOne.setParent(this);
		
		//Find the condition from the select.
		String conditionStatement = selectionParts.get(
			QueryParser.SELECT_WHERE_INDEX);
		setCondition(Condition.makeCondition(conditionStatement));	
	}
	
	@Override
	public long calculateCost() {
		
		long br = tableOne.calculateCost();
		double div = calculateCostHelper(br, this.getCondition());
		if (div%1 == 0) return (long)div;
		else return ((long)div + 1);
	}
	
	public double calculateCostHelper(double numblox, Condition con){
		if (con.getCondition().equals(QueryParser.LESS_THAN) || 
				con.getCondition().equals(QueryParser.GREATER_THAN)){
			return (numblox * 1.0)/3.0;
		} else if (con.getCondition().equals(QueryParser.EQUAL)){
			long leftuniquevals = tableOne.uniqueVals(con.getAttributes().get(0));
			long rightuniquevals = tableOne.uniqueVals(con.getAttributes().get(1));
			double totalvals = leftuniquevals * rightuniquevals;
			return (numblox * 1.0)/totalvals;
		} else if (con.getCondition().equals(QueryParser.AND)){
			String left = (AndOrCondition.parseLeftHand(con.getCondition()));
			String right = (AndOrCondition.parseRightHand(con.getCondition()));
			return calculateCostHelper(numblox, Condition.makeCondition(left)) * calculateCostHelper(numblox, Condition.makeCondition(right));
			
		} else if (con.getCondition().equals(QueryParser.OR)){
			String left = (AndOrCondition.parseLeftHand(con.getCondition()));
			String right = (AndOrCondition.parseRightHand(con.getCondition()));
			return calculateCostHelper(numblox, Condition.makeCondition(left)) + calculateCostHelper(numblox, Condition.makeCondition(right));
		}
		return 0;
	}
	
	public long uniqueVals(String att){
		return tableOne.uniqueVals(att);
	}
	
	/**This method will return whether or not the Select allows children as
	 * per the <code>TreeNode</code> interface
	 * @return <code><b>true</b></code> becase a Select must always 
	 * have children.
	 */
	public boolean getAllowsChildren() {
		return true;
	}
	
	/**This method will return the number of children that this Select has
	 * as per the TreeNode interface.
	 * @return The number of children of this node.
	 */
	public int getChildCount() {
		int childCount = 1 + tableOne.getChildCount();
		return childCount;
	}
	
	/**This method returns the value of condition.
	 * @return the condition
	 */
	public Condition getCondition() {
		return condition;
	}
	
	
	/**Says whether or not the Selection is a Leaf.  Always false, a Select is 
	 * never a Leaf in a query tree.
	 * @return <code<b>false</b></code> because select statements aren't leaves.
	 */
	public boolean isLeaf() {
		return false;
	}

	/**This method will set the value of condition.
	 * @param newCondition The new value of condition.
	 */
	public void setCondition(final Condition newCondition) {
		this.condition = newCondition;
	}

	public String toString() {
		
		String string = "|"; 
		
		string += this.queryID + "\t|";
		string += this.executionOrder + "\t|";
		string += this.type + "\t\t|";
		string += condition.toString() + "\t|";
		string += tableOneAccess +"\t|";
		string += resultTableID + "\t|";
		string += "\n";
		
		return string;
	}
}
