package queries;

public class CrossJoin extends Operation {

	protected int tableTwoAccess;
	
	protected int tableTwoID;
	
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
		//TODO fix this so it does something.
		return 0;
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
		string += tableTwoID + "\t";
		string += tableOneAccess +"\t";
		string += tableTwoAccess +"\t";
		string += "\t";
		string += resultTableID;
		
		return string;
	}


}
