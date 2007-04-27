package queries;

import java.util.ArrayList;

import dbase.Attribute;
import dbase.Database;
import dbase.Iterator;
import dbase.Relation;
import dbase.RelationHolder;

public class Join extends Operation {

	protected Condition condition;
	
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
	
	public ArrayList < String > getTreeAttributes() {
		
		//TODO Also get the attributes from the condition
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
	
	/**This method will return all of the SimpleConditions of this join,
	 * and below it.
	 * @return The SimpleConditions of this Join and of nodes below it.
	 */
	public ArrayList < SimpleCondition > getTreeConditions() {
		
		//Get the ones for this join
		ArrayList < SimpleCondition > conditions = condition.getConditions();
		
		//The tableOne
		conditions.addAll(tableOne.getTreeConditions());
		
		//The tableTwo
		conditions.addAll(tableTwo.getTreeConditions());
		
		return conditions;
	}
	
	/**Says whether or not the Join is a Leaf.  
	 * Always false, a Join is never a Leaf in a query tree.
	 * @return <code><b>false</b></code> because Join statements
	 * aren't leaves.
	 */
	public boolean isLeaf() {
		return false;
	}
	
	public boolean execute(){
		if (! (tableOne.execute() && tableTwo.execute())){
			return false;
		} else {
			Relation r1 = RelationHolder.getRelationHolder().getRelation(tableOne.getResultTableID());
			Relation r2 = RelationHolder.getRelationHolder().getRelation(tableTwo.getResultTableID());
			ArrayList<Attribute> atts = r1.getAttributes();
			atts.addAll(r2.getAttributes());
			ArrayList<String> types =  new ArrayList<String>();
			ArrayList<String> names =  new ArrayList<String>();
			for (int j=0; j<atts.size(); j++){
				types.add(atts.get(j).getType().name());
				names.add(atts.get(j).getName());
			}
			
			String[] types2 =  new String[0];
			types2= types.toArray((new String[0]));
			String[] names2 =  new String[0];
			names2= names.toArray((new String[0]));
			
			Iterator i1 = new Iterator(r1);
			Iterator i2 = new Iterator(r2);
			
			
			while (i1.hasNext()){
				String[] r1vals = i1.getNext();
				while (i2.hasNext()){
					String[] r2vals = i2.getNext();
					String[] allvals = new String[r1vals.length+r2vals.length];
					for (int j=0; j<r1vals.length; j++){
						allvals[j] = r1vals[j];
					}
					for (int j=r1vals.length; j<(r1vals.length+r2vals.length); j++){
						allvals[j]= r2vals[j-r1vals.length];
					}
					if (condition.compare(names2, allvals, types2)){
						Database.getCatalog().insert(this.resultTableID, names2, allvals);
					}
					
				}
				
			}
			
			return false;
		}
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
	
	public ArrayList<String> getAttributes(){
		ArrayList <String> attrs = tableOne.getAttributes();
		attrs.addAll(tableTwo.getAttributes());
		return attrs;
	}
	
	/**This method will generate the table schema that results from the
	 * join we have going on here.
	 */
	public void generateTemporaryTable() {
		
		//From the RelationHolder, get the IDs of the source relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		Relation left = holder.getRelation(tableOne.getResultTableID());
		Relation right = holder.getRelation(tableTwo.getResultTableID());
		
		//Get the attributes
		ArrayList < Attribute > leftAttributes = left.getAttributes();
		ArrayList < Attribute > rightAttributes = right.getAttributes();
		
		//Create the resulting relation
		Relation result = new Relation(QueryParser.RESULT + resultTableID,
			resultTableID);
		
		//For the leftHand attributes
		for (int leftIndex = 0;
			leftIndex < leftAttributes.size(); leftIndex++) {
			
			//Get the attribute to work on
			Attribute leftAttribute =  leftAttributes.get(leftIndex);
			String leftName = leftAttribute.getName();
			
			boolean added = false;
			
			//See if it has the same name as any on the right
			for (int rightIndex = 0; 
				rightIndex < rightAttributes.size(); rightIndex++) {
				
				Attribute rightAttribute = rightAttributes.get(rightIndex);
				String rightName = rightAttribute.getName();
				
				if (leftName.equalsIgnoreCase(rightName)) {
					Relation source = holder.getRelation(leftAttribute.getParent());
					leftName = source.getName() + "." + leftName;
					Attribute newAttribute = new Attribute(
							leftName, leftAttribute.getType(), 0);
					result.addAttribute(newAttribute);
					added = true;
					break;
				}
			}
			
			if (!added) {
				result.addAttribute(leftAttribute);
			}
		}
		
		//For the rightHand attributes
		for (int rightIndex = 0;
			rightIndex < rightAttributes.size(); rightIndex++) {
			
			//Get the attribute to work on
			Attribute rightAttribute =  rightAttributes.get(rightIndex);
			String rightName = rightAttribute.getName();
			
			boolean added = false;
			
			//See if it has the same name as any on the right
			for (int leftIndex = 0; 
				leftIndex < leftAttributes.size(); leftIndex++) {
				
				Attribute leftAttribute = leftAttributes.get(leftIndex);
				String leftName = leftAttribute.getName();
				
				if (rightName.equalsIgnoreCase(leftName)) {
					//Find the relation that rightName belongs to.
					Relation source = holder.getRelation(rightAttribute.getParent());
					rightName = source.getName() + "." + rightName;
					//Then create and add it to the new relation
					Attribute newAttribute = new Attribute(
						rightName, rightAttribute.getType(), 0);
					result.addAttribute(newAttribute);
					added = true;
					break;
				}
			}
			
			if (!added) {
				result.addAttribute(rightAttribute);
			}
			
		
		}
		
		holder.addRelation(result);
	}
	
	
}
