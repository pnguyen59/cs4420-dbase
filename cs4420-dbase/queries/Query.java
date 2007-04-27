package queries;

import java.util.ArrayList;

public class Query {

	private Operation treeRoot;
	
	public Query(final String newQuery) {
		
		setTreeRoot(Operation.makeOperation(newQuery));
		
	}

	/**This method returns the value of treeRoot.
	 * @return the treeRoot
	 */
	public Operation getTreeRoot() {
		return treeRoot;
	}

	/**This method will set the value of treeRoot.
	 * @param newTreeRoot The new value of treeRoot.
	 */
	public void setTreeRoot(final Operation newTreeRoot) {
		this.treeRoot = newTreeRoot;
	}
	
	public ArrayList < String > getRelations() {
		
		ArrayList < String > relations = treeRoot.getRelations();
		
		return null;
	}
	
	public ArrayList < String > getAttributes() {
		
		return null;
	}
	
	
	
}
