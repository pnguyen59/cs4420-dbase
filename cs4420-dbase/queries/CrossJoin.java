package queries;

import java.util.ArrayList;

public class CrossJoin extends Operation {

	
	public CrossJoin(final String statement) {
		
		//Get the relation names out of the statement
		ArrayList < String > tables = QueryParser.parseRelationNames(statement);
		
		//Make the first table a TableOperation, because regardless of the
		//Number of other joins needed, this one will still join with one
		//real table
		tableOne = new TableOperation(tables.get(0));
		
		//See how many cross joins are needed to get the results
		if (tables.size() > 2) {
			//Make the tables left as a crossjoin
			String newCrossJoin = "(CROSSJOIN ";
			for (int index = 1; index < tables.size(); index++) {
				newCrossJoin += "\"" + tables.get(index) + "\", "; 
			}
			newCrossJoin += ")";
			tableTwo = Operation.makeOperation(newCrossJoin);
		} else {
			//Otherwise make it the second table
			tableTwo = new TableOperation(tables.get(1));
		}
		
		//Regardless, make both tableOne and tableTwo's parents this.
		tableOne.setParent(this);
		tableTwo.setParent(this);
	}
	
	@Override
	public long calculateCost() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**This method will return whether or not the CrossJoin allows children as
	 * per the <code>TreeNode</code> interface.
	 * @return <code><b>true</b></code> becase a CrossJoin must always 
	 * have children.
	 */
	public boolean getAllowsChildren() {
		return true;
	}
	
	/**This method will return the number of children that this CrossJoin has
	 * as per the TreeNode interface.
	 * @return The number of children of this node.
	 */
	public int getChildCount() {
		
		int children = 2;
		children += tableOne.getChildCount();
		children += tableTwo.getChildCount();
		
		return children;
	}
	
	/**Says whether or not the CrossJoin is a Leaf.  
	 * Always false, a CrossJoin is never a Leaf in a query tree.
	 * @return <code><b>false</b></code> because CrossJoin statements
	 * aren't leaves.
	 */
	public boolean isLeaf() {
		return false;
	}

	public String toString() {
		
		String string = ""; 
		
		string += this.queryID + "\t";
		string += this.executionOrder + "\t";
		string += this.type + "\t";
		string += "\t";
		string += tableOneAccess +"\t";
		string += tableTwoAccess +"\t";
		string += "\t";
		string += resultTableID;
		
		return string;
	}


}
