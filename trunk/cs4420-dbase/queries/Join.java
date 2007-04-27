package queries;

import java.util.ArrayList;

public class Join extends Operation {

	protected Condition conditions;
	
	protected int tableTwoAccess;
	
	protected int tableTwoID;
	
	@Override
	public long calculateCost() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**This method will return whether or not the Join allows children as
	 * per the <code>TreeNode</code> interface.
	 * @return <code><b>true</b></code> becase a Join must always 
	 * have children.
	 */
	public boolean getAllowsChildren() {
		return true;
	}
	
	/**This method will return the number of children that this Join has
	 * as per the TreeNode interface.
	 * @return The number of children of this node.
	 */
	public int getChildCount() {
		//TODO fix this so it does something.
		return 0;
	}
	
	public ArrayList < String > getRelations() {
		
		ArrayList < String > relations = new ArrayList < String > ();
		
		ArrayList < String > subOperation;
		
		//If this is a one level dealie, that is the tableOne is just a regular
		//old table then just return that name
		if (tableOne.getType().equalsIgnoreCase(QueryParser.TABLEOPERATION)) {
			relations.add(((TableOperation) tableOne).getTableName());
		} else { //Otherwise, ask the stuff below it for its tables
			relations = tableOne.getRelations(); 
		}
	
		//Now check on the second table
		if (tableTwo.getType().equalsIgnoreCase(QueryParser.TABLEOPERATION)) {
			relations.add(((TableOperation) tableTwo).getTableName());
		} else {
			subOperation = tableOne.getRelations();
			//Merge the listos
			for (int sub = 0; sub < subOperation.size(); sub++) {
				relations.add(subOperation.get(sub));
			}
		}
		
		return relations;
	}
	
	/**Says whether or not the Join is a Leaf.  
	 * Always false, a Join is never a Leaf in a query tree.
	 * @return <code><b>false</b></code> because Join statements
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
		string += tableTwoID + "\t";
		string += tableOneAccess +"\t";
		string += tableTwoAccess +"\t";
		string += "\t";
		string += resultTableID;
		
		return string;
	}
	
	public long uniqueVals(String att){ 
		if (tableOne.containsAttribute(att)){ 
			return tableOne.uniqueVals(att);
		} else if (tableTwo.containsAttribute(att)){
			return tableTwo.uniqueVals(att);
		} else {
			return -1;
		}
	}
}
