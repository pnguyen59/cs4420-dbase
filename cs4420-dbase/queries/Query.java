package queries;

import java.util.ArrayList;

import dbase.Relation;
import dbase.RelationHolder;

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
		
		return treeRoot.getRelations();
	}
	
	public ArrayList < String > getAttributes() {
		
		return treeRoot.getTreeAttributes();
	}
	
	public ArrayList < SimpleCondition > getConditions() {
		
		return treeRoot.getTreeConditions();
	}
	

	public void printQueryTree() {
		ArrayList < Operation > nodes = treeRoot.getPostOrder();
		for (int index = 0; index < nodes.size(); index++) {
			System.out.print(nodes.get(index).toString());
		}
	}

	public int getResultRelationID(){
		return treeRoot.getResultTableID();
	}

	public void assignTemporaryTables() {
		
		int tempTable = RelationHolder.getRelationHolder().getSmallestUnusedID();
		String temp = "TEMP";
		int step = 1;
		
		ArrayList < Operation > tree = treeRoot.getPostOrder();
		
		for (int index = 0; index < tree.size(); index++) {
			
			Operation currentOperation = tree.get(index);
			
			if (!(currentOperation.getType().
				equalsIgnoreCase(QueryParser.TABLEOPERATION))) {
				currentOperation.setResultTableID(tempTable);
				currentOperation.setExecutionOrder(step);
				step++;
				tempTable++;
			}
		}
	}
	
	public void generateTemporaryTables() {
		
		//Get the Post-order version of the tree
		ArrayList < Operation > tree = treeRoot.getPostOrder();
		
		for (int index = 0; index < tree.size(); index++) {
			
			Operation currentOperation = tree.get(index);
			//Tell the currentOperation to make its temporary table
			currentOperation.generateTemporaryTable();
			
			Relation relation = RelationHolder.getRelationHolder().
				getRelation(currentOperation.getResultTableID());
			System.out.println(relation);
		}
		
	}
	
	public boolean execute(){
		return treeRoot.execute();
	}
	
}
