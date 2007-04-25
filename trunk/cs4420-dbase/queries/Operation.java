package queries;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

public abstract class Operation implements TreeNode  {
	
	/**This method will make an Operation from the literal represenation
	 * of that operation.
	 * @param operation The literal represenation of the operation.
	 * @return The Operation formed from the literal.
	 */
	public static Operation makeOperation(final String operation) {
		
		//Remove the spaces, open parens and make this thing upper case
		String upperCase = operation.toUpperCase();
		String noSpaces = upperCase.replace(" ", "");
		String noParens = noSpaces.replace("(", "");
		
		//See what kind of operation this is
		String [] split = noParens.split("/\\)/");
		String first = split[0];
		if (first.equalsIgnoreCase(QueryParser.PROJECT)) {
			//return new Project(upperCase);
		} else if (first.equalsIgnoreCase(QueryParser.CROSSJOIN)) {
			//return new CorssJoin(upperCase);
		} else if (first.equalsIgnoreCase(QueryParser.SELECT)) {
			return new Select(upperCase);
		} else {
			
		}
		
		return null;
	}
	
	protected int executionOrder;
	
	protected Operation parent;
	
	protected int queryID;
	
	protected int resultTableID;
	
	/**The Operation from whence this one gets its data, a child.*/
	protected Operation tableOne;
	
	protected int tableOneAccess;
	
	protected String type;

	public abstract long calculateCost();
	
	/**This does something I don't understand.
	 * @return Stuff.
	 */
	public Enumeration children() {
		return null;
	}
	
	/**Gets a child at an index.  Don't know how that works.
	 * @param index Index of the child.
	 * @return nothing.
	 */
	public TreeNode getChildAt(final int index) {
		return null;
	}
	
	
	/**This method returns the value of executionOrder.
	 * @return the executionOrder
	 */
	public int getExecutionOrder() {
		return executionOrder;
	}	
	
	/**The index of a Child.  Don't know how that works.
	 * @param child The child to find the index of
	 * @return nothing.
	 */
	public int getIndex(final TreeNode child) {
		return 0;
	}

	/**This returns the parent of this Select, probably another operation, but
	 * return it as a <code>TreeNode</code> because of the interface.
	 * @return The Operation that owns this one.
	 */
	public TreeNode getParent() {
		return this.parent;
	}


	/**This method returns the value of queryID.
	 * @return the queryID
	 */
	public int getQueryID() {
		return queryID;
	}


	/**This method returns the value of resultTableID.
	 * @return the resultTableID
	 */
	public int getResultTableID() {
		return resultTableID;
	}


	/**This method returns the value of tableOne.
	 * @return the tableOne
	 */
	public Operation getTableOne() {
		return tableOne;
	}


	/**This method returns the value of tableOneAccess.
	 * @return the tableOneAccess
	 */
	public int getTableOneAccess() {
		return tableOneAccess;
	}


	/**This method returns the value of type.
	 * @return the type
	 */
	public String getType() {
		return type;
	}


	/**This method will set the value of executionOrder.
	 * @param newExecutionOrder The new value of executionOrder.
	 */
	public void setExecutionOrder(final int newExecutionOrder) {
		this.executionOrder = newExecutionOrder;
	}


	/**This method will set the value of parent.
	 * @param newParent The new value of parent.
	 */
	public void setParent(final Operation newParent) {
		this.parent = newParent;
	}


	/**This method will set the value of queryID.
	 * @param newQueryID The new value of queryID.
	 */
	public void setQueryID(final int newQueryID) {
		this.queryID = newQueryID;
	}


	/**This method will set the value of resultTableID.
	 * @param newResultTableID The new value of resultTableID.
	 */
	public void setResultTableID(final int newResultTableID) {
		this.resultTableID = newResultTableID;
	}


	/**This method will set the value of tableOne.
	 * @param newTableOne The new value of tableOne.
	 */
	public void setTableOne(final Operation newTableOne) {
		this.tableOne = newTableOne;
	}


	/**This method will set the value of tableOneAccess.
	 * @param newTableOneAccess The new value of tableOneAccess.
	 */
	public void setTableOneAccess(final int newTableOneAccess) {
		this.tableOneAccess = newTableOneAccess;
	}


	public void setType(final String newType) {
		this.type = newType;
	}
	
}
