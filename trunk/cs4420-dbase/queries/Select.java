package queries;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.locks.Condition;

import javax.swing.tree.TreeNode;


public class Select extends Operation {

	protected Condition condition;
	
	/**This will create a new instance of Select.  It will use the 
	 * statement passed to it to find out what the condition of the select is
	 * and what it is selecting from.
	 * @param selectStatement The definition of the select.
	 */
	public Select (final String selectStatement) {
		
		//Tet the list of the parts of this select
		ArrayList < String > selectionParts = 
			QueryParser.parseStatementParts(selectStatement);
		
		//Find out what this thing is selecting from
		
		
		//Find the condition from the select.
		
		
	}
	
	@Override
	public long calculateCost() {
		// TODO Auto-generated method stub
		return 0;
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
		//TODO fix this so it does something.
		return 0;
	}
	
	/**Says whether or not the Selection is a Leaf.  Always false, a Select is 
	 * never a Leaf in a query tree.
	 * @return <code<b>false</b></code> because select statements aren't leaves.
	 */
	public boolean isLeaf() {
		return false;
	}
	
	public String toString() {
		
		String string = ""; 
		
		string += this.queryID + "\t";
		string += this.executionOrder + "\t";
		string += this.type + "\t";
		string += condition.toString();
		string += tableOneAccess +"\t\t";
		string += "\t";
		string += resultTableID;
		
		return string;
	}
}
