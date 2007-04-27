package queries;

import java.util.ArrayList;

import dbase.Attribute;
import dbase.Relation;
import dbase.RelationHolder;

public class CrossJoin extends Operation {

	
	public CrossJoin(final String statement) {
		
		setType(QueryParser.CROSSJOIN);
		
		//Get the relation names out of the statement
		ArrayList < String > tables = QueryParser.parseRelationNames(statement);
		
		//Make the first table a TableOperation, because regardless of the
		//Number of other joins needed, this one will still join with one
		//real table
		tableOne = Operation.makeOperation(tables.get(0));
		
		//See how many cross joins are needed to get the results
		if (tables.size() > 2) {
			//Make the tables left as a crossjoin
			String newCrossJoin = "(CROSSJOIN ";
			for (int index = 1; index < tables.size(); index++) {
				newCrossJoin += tables.get(index);
				//If it is the last one, don't add the comma
				if (!(index == tables.size() - 1)) {
					newCrossJoin += ",";
				}
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
		
		return tableOne.calculateCost()*tableTwo.calculateCost();
	}
	
	/**This method will generate the table schema that results from the
	 * join we have going on here.
	 */
	public void generateTemporaryTable() {
		
		//From the RelationHolder, get the IDs of the source relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		Relation left = holder.getRelation(tableOne.getResultTableID());
		Relation right = holder.getRelation(tableTwo.getResultTableID());
		
		//Create the resulting relation
		Relation result = new Relation(QueryParser.RESULT + resultTableID,
			resultTableID);
		
		//Add the attributes of each to the result
		ArrayList < Attribute > attributes = left.getAttributes();
		for (int index = 0; index < attributes.size(); index++) {
			result.addAttribute(attributes.get(index));
		}
		attributes = right.getAttributes();
		for (int index = 0; index < attributes.size(); index++) {
			result.addAttribute(attributes.get(index));
		}
	}
	
	/**This method will return whether or not the CrossJoin allows children as
	 * per the <code>TreeNode</code> interface.
	 * @return <code><b>true</b></code> becase a CrossJoin must always 
	 * have children.
	 */
	public boolean getAllowsChildren() {
		return true;
	}
	
	public ArrayList<String> getAttributes(){
		ArrayList <String> attrs = tableOne.getAttributes();
		attrs.addAll(tableTwo.getAttributes());
		return attrs;
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

	public ArrayList < String > getRelations() {
		
		ArrayList < String > relations = new ArrayList < String > ();
		
		ArrayList < String > subOperation;
		
		relations = tableOne.getRelations();
		relations.addAll(tableTwo.getRelations());
		
		return relations;
	}
	
	public ArrayList < String > getTreeAttributes() {
		
		//Get the list of attributes from tableOne
		ArrayList < String > tableOneAttributes = tableOne.getTreeAttributes();
		ArrayList < String > tableTwoAttributes = tableTwo.getTreeAttributes();
		
		//Merge and return the list
		for (int index = 0; index < tableOneAttributes.size(); index++) {
			
			boolean add = true;
			for (int inner = 0; inner < tableTwoAttributes.size(); inner++) {
				if (tableOneAttributes.get(index) 
					== tableTwoAttributes.get(inner)) {
					add = false;
				}
			}
			if (add) {
				tableTwoAttributes.add(tableOneAttributes.get(index));
			}
		}
		
		return tableTwoAttributes;
	}

	/**This method will return all of the SimpleConditions of the nodes (
	 * Operations) below this CrossJoin
	 * @return The SimpleConditions of the nodes below this CrossJoin.
	 */
	public ArrayList < SimpleCondition > getTreeConditions() {
		
		//Get the ones for this join
		ArrayList < SimpleCondition > conditions = 
			new ArrayList < SimpleCondition > ();
		
		//The tableOne
		conditions.addAll(tableOne.getTreeConditions());
		
		//The tableTwo
		conditions.addAll(tableTwo.getTreeConditions());
		
		return conditions;
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
		
		String string = "|"; 
		
		string += this.queryID + "\t|";
		string += this.executionOrder + "\t|";
		string += this.type + "\t|";
		string += tableOne.getResultTableID() + "\t|";
		string += tableTwo.getResultTableID() + "\t|";
		string += resultTableID + "\t|";
		string += "\n";
		
		return string;
	}
}
