package queries;

import java.util.ArrayList;

public class Project extends Operation {

	protected ArrayList < String > attributes;
	
	/**This will create a new instance of Project.  It will find what to 
	 * project and from where from the statement passed.
	 * @param statement The literal of the project statement.
	 */
	public Project (final String statement) {
		
		setType(QueryParser.PROJECT);
		
		ArrayList < String > parts = QueryParser.parseStatementParts(statement);
		
		for (int index = 0; index < parts.size(); index++) {
			System.out.println(parts.get(index));
		}
		
		//Find the list of attributes to project
		attributes = QueryParser.parseQueryAttributes(
			parts.get(QueryParser.PROJECT_ATTRIBUTES_INDEX));
		
		//Find the table its coming from
		tableOne = Operation.makeOperation(
			parts.get(QueryParser.PROJECT_FROM_INDEX));
		tableOne.setParent(this);
	}
	
	/**This method will return the cost of performing this Project which is
	 * basically the size of the table it is projecting from in blocks.
	 * @return The cost of this Project.
	 */
	public long calculateCost() {
		return 0;
	}
	
	/**This method will return whether or not the Project allows children as
	 * per the <code>TreeNode</code> interface.
	 * @return <code><b>true</b></code> becase a Project must always 
	 * have children.
	 */
	public boolean getAllowsChildren() {
		return true;
	}
	
	/**This method returns the value of attributes.
	 * @return the attributes
	 */
	public ArrayList < String > getAttributes() {
		return attributes;
	}
	
	/**This method will return the number of children that this Project has
	 * as per the TreeNode interface.
	 * @return The number of children of this node.
	 */
	public int getChildCount() {
		int childCount = 1 + tableOne.getChildCount();
		return childCount;
	}
	
	/**Says whether or not the Projection is a Leaf.  
	 * Always false, a Project is never a Leaf in a query tree.
	 * @return <code><b>false</b></code> because Project statements
	 * aren't leaves.
	 */
	public boolean isLeaf() {
		return false;
	}

	/**This method will set the value of attributes.
	 * @param newAttributes The new value of attributes.
	 */
	public void setAttributes(final ArrayList < String > newAttributes) {
		this.attributes = newAttributes;
	}

	public String toString() {
		
		String string = "|"; 
		
		string += this.queryID + "\t|";
		string += this.executionOrder + "\t|";
		string += this.type + "\t|";
		for (int index = 0; index < attributes.size(); index++) {
			string += attributes.get(index) + ", ";
		}
		string += "\t\t|";
		string += tableOneAccess +"\t|";
		string += resultTableID + "\t|";
		string += "\n";
		
		return string;
	}
	
}
