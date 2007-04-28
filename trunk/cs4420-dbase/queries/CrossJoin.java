package queries;

import java.util.ArrayList;

import dbase.Attribute;
import dbase.Database;
import dbase.Iterator;
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
	
	public boolean execute(){
		System.out.println("TYPES: "+tableOne.getType()+" "+tableTwo.getType());
		if (! (tableOne.execute() && tableTwo.execute())){
			System.out.println("In Here?");
			return false;
		} else {
			Relation r1 = RelationHolder.getRelationHolder().getRelation(tableOne.getResultTableID());
			Relation r2 = RelationHolder.getRelationHolder().getRelation(tableTwo.getResultTableID());
			System.out.println("SZZ: "+r1.getAttributes().size());
			ArrayList<Attribute> atts = (ArrayList <Attribute>)r1.getAttributes().clone();
			atts.addAll(r2.getAttributes());
			System.out.println("CROSSJOIN ATT SIZE: "+atts.size());
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
				System.out.println("R1: "+Utilities.printArray(r1vals));
				while (i2.hasNext()){
					
					String[] r2vals = i2.getNext();
					System.out.println("R2: "+Utilities.printArray(r2vals));
					String[] allvals = new String[r1vals.length+r2vals.length];
					for (int j=0; j<r1vals.length; j++){
						allvals[j] = r1vals[j];
					}
					for (int j=r1vals.length; j<(r1vals.length+r2vals.length); j++){
						allvals[j]= r2vals[j-r1vals.length];
					}
					System.out.println("VALS: "+Utilities.printArray(allvals));
						Database.getCatalog().insert(this.resultTableID, names2, allvals);
					System.out.println("Added something to CrossJoin");
					
				}
				
			}
			
			return true;
		}
	}
	
	/**This method will generate the table schema that results from the
	 * join we have going on here.
	 */
	public void generateTemporaryTable() {
		
		//From the RelationHolder, get the IDs of the source relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		Relation left = holder.getRelation(tableOne.getResultTableID());
		Relation right = holder.getRelation(tableTwo.getResultTableID());
		Relation result = new Relation(QueryParser.RESULT + resultTableID,
			resultTableID);
		
		//Get the attributes from both sides
		ArrayList < Attribute > rightAttributes = right.getAttributes();
		
		//Get the attributes
		ArrayList < Attribute > leftAttributes = left.getAttributes();
		
		System.out.println("GENERATING TEMPORARY TABLES: Left size "+tableOne.resultTableID+" Right size "+tableTwo.resultTableID);
		
		//Merge the two lists
		ArrayList < Attribute > resultAttributes = 
			(ArrayList) rightAttributes.clone();
		resultAttributes.addAll(leftAttributes);
		
		//Qualify all of the Attributes
		ArrayList < Attribute > qualifiedAttributes 
			= new ArrayList < Attribute > ();
		for (int index = 0; index < resultAttributes.size(); index++) {
			
			//Get the current attribute and qualify it if it isn't already
			Attribute currentAttribute = resultAttributes.get(index);
			if (currentAttribute.getName().contains(".")) {
				qualifiedAttributes.add(currentAttribute);
				continue;
			}
			//System.out.println("CURRENT PARENT ID:"  + currentAttribute.getParent());
			Relation parent = holder.getRelation(currentAttribute.getParent());
			//System.out.println("CURRENT CROSSJOIN ATT: " + currentAttribute);
			String parentName = parent.getName();
			String qualifiedName = parentName + "." 
				+ currentAttribute.getName();
 			Attribute newAttribute = new Attribute(qualifiedName,
 				currentAttribute.getType(), 0);
 			newAttribute.setParent(parent.getID());
 			
 			//Add it to the list to return
			qualifiedAttributes.add(newAttribute);
		}
		
		//Set the resutl tables attributes
		result.setAttributes(qualifiedAttributes);
		
		holder.addRelation(result);
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
		ArrayList <String> attrs = (ArrayList) tableOne.getAttributes().clone();
		attrs.addAll(tableTwo.getAttributes());
		return (ArrayList) attrs.clone();
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

	/**This just returns the required attributes of its parents.
	 * @return A horses head in Chris's bed.
	 */
	public ArrayList < String > getParentAttributes() {
		return parent.getParentAttributes();
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
