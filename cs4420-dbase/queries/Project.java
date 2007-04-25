package queries;

import java.util.ArrayList;

import dbase.Attribute;

public class Project extends Operation {

	protected ArrayList < Attribute > attributes;
	
	public long calculateCost() {
		return 1000;
	}
	
	public String toString() {
		
		String string = ""; 
		
		string += this.queryID + "\t";
		string += this.executionOrder + "\t";
		string += this.type + "\t";
		for (int index = 0; index < attributes.size(); index++) {
			string += attributes.get(index).getName() + ", ";
		}
		string += tableOneAccess +"\t\t";
		string += "\t";
		string += resultTableID;
		
		return string;
	}
	
	/**This method will return whether or not the Project allows children as
	 * per the <code>TreeNode</code> interface.
	 * @return <code><b>true</b></code> becase a Project must always 
	 * have children.
	 */
	public boolean getAllowsChildren() {
		return true;
	}
	
	/**This method will return the number of children that this Project has
	 * as per the TreeNode interface.
	 * @return The number of children of this node.
	 */
	public int getChildCount() {
		//TODO fix this so it does something.
		return 0;
	}
	
	/**Says whether or not the Projection is a Leaf.  
	 * Always false, a Project is never a Leaf in a query tree.
	 * @return <code><b>false</b></code> because Project statements
	 * aren't leaves.
	 */
	public boolean isLeaf() {
		return false;
	}
	
}
