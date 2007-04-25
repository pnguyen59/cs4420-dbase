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
	
	protected String type;
	
	protected int queryID;
	
	protected int executionOrder;
	
	protected int tableOneAccess;
	
	protected int resultTableID;
	
	protected Operation parent;

	public void setType(final String newType) {
		this.type = newType;
	}
	
	
	/**This returns the parent of this Select, probably another operation, but
	 * return it as a <code>TreeNode</code> because of the interface.
	 * @return The Operation that owns this one.
	 */
	public TreeNode getParent() {
		return this.parent;
	}
	
	/**The index of a Child.  Don't know how that works.
	 * @child The child to find the index of
	 * @return nothing.
	 */
	public int getIndex(final TreeNode child) {
		return 0;
	}
	
	
	/**Gets a child at an index.  Don't know how that works.
	 * @param index Index of the child.
	 * @return nothing.
	 */
	public TreeNode getChildAt(final int index) {
		return null;
	}	
	
	/**This does something I don't understand.
	 * @return Stuff.
	 */
	public Enumeration children() {
		return null;
	}

	public abstract long calculateCost();
}
