package queries;

import java.util.ArrayList;

import dbase.Attribute;
import dbase.Database;
import dbase.Iterator;
import dbase.Relation;
import dbase.RelationHolder;


public class Select extends Operation {
	
	protected Condition condition;
	
	/**This will create a new instance of Select.  It will use the 
	 * statement passed to it to find out what the condition of the select is
	 * and what it is selecting from.
	 * @param selectStatement The definition of the select.
	 */
	public Select(final String selectStatement) {
		
		setType(QueryParser.SELECT);
		
		//Tet the list of the parts of this select
		ArrayList < String > selectionParts = 
			QueryParser.parseStatementParts(selectStatement);
		
		//Find out what this thing is selecting from
		String sourceStatement = selectionParts.get(
			QueryParser.SELECT_FROM_INDEX);
		setTableOne(Operation.makeOperation(sourceStatement));
		tableOne.setParent(this);
		//System.out.println("SELECT TABLE ONE TYPE: " + tableOne.getType());
		
		//Find the condition from the select if there is one
		if (selectionParts.size() > 1) {
			String conditionStatement = selectionParts.get(
			QueryParser.SELECT_WHERE_INDEX);
			setCondition(Condition.makeCondition(conditionStatement));
		}
	}
	
	public Select()
	{
		setType(QueryParser.SELECT);
	}
	
	public boolean execute(){
		if (tableOne.execute()){
			//do the select crapola here
			Relation rel = RelationHolder.getRelationHolder().getRelation(tableOne.getResultTableID());
			ArrayList<Attribute> atts = rel.getAttributes();
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
			Relation rel2 = RelationHolder.getRelationHolder().getRelation(this.resultTableID);
			if (rel2.getAttributes().size()==0){
				//hasn't been initialized
				for (int j=0; j<atts.size(); j++){
					rel2.addAttribute(atts.get(j));
				}
			}
			Iterator it = new Iterator(rel);
			while (it.hasNext()){
				String[] vals = it.getNext();
				if (condition.compare(names2, vals, types2)){
					
					Database.getCatalog().insert(this.resultTableID,names2,vals);
				}
			}
			
			return true;
		} else{
			return false;
		}
		
	}
	
	@Override
	public long calculateCost() {
		
		long br = tableOne.calculateCost();
		double div = calculateCostHelper(br, this.getCondition());
		if (div%1 == 0) return (long)div;
		else return ((long)div + 1);
	}
	
	public double calculateCostHelper(double numblox, Condition con){
		if (con.getCondition().equals(QueryParser.LESS_THAN) || 
				con.getCondition().equals(QueryParser.GREATER_THAN)){
			return (numblox * 1.0)/3.0;
		} else if (con.getCondition().equals(QueryParser.EQUAL)){
			long leftuniquevals = tableOne.uniqueVals(con.getAttributes().get(0));
			long rightuniquevals = tableOne.uniqueVals(con.getAttributes().get(1));
			double totalvals = leftuniquevals * rightuniquevals;
			return (numblox * 1.0)/totalvals;
		} else if (con.getCondition().equals(QueryParser.AND)){
			String left = (AndOrCondition.parseLeftHand(con.getCondition()));
			String right = (AndOrCondition.parseRightHand(con.getCondition()));
			return calculateCostHelper(numblox, Condition.makeCondition(left)) * calculateCostHelper(numblox, Condition.makeCondition(right));
			
		} else if (con.getCondition().equals(QueryParser.OR)){
			String left = (AndOrCondition.parseLeftHand(con.getCondition()));
			String right = (AndOrCondition.parseRightHand(con.getCondition()));
			return calculateCostHelper(numblox, Condition.makeCondition(left)) + calculateCostHelper(numblox, Condition.makeCondition(right));
		}
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
		int childCount = 1 + tableOne.getChildCount();
		return childCount;
	}
	
	/**This method returns the value of condition.
	 * @return the condition
	 */
	public Condition getCondition() {
		return condition;
	}
	
	public ArrayList < String > getRelations() {
		
	ArrayList < String > relations = new ArrayList < String > ();
		
		//If this is a one level dealie, that is the tableOne is just a regular
		//old table then just return that name
		if (tableOne.getType().equalsIgnoreCase(QueryParser.TABLEOPERATION)) {
			relations.add(((TableOperation) tableOne).getTableName());
		} else { //Otherwise, ask the stuff below it for its tables
			relations = tableOne.getRelations(); 
		}
		
		return relations;
	}
	
	public ArrayList<String> getAttributes(){
		return condition.getAttributes();
	}
	
	public ArrayList < String > getTreeAttributes() {
		
		//Get the attributes of this thing
		ArrayList < String > attributes;
		if (condition != null) {
			attributes = condition.getAttributes();
		} else {
			attributes = new ArrayList < String > ();
		}
		
		//Merge the list of attributes from this one and those below it
		ArrayList < String > subAttributes = tableOne.getTreeAttributes();
		
		//Merge and return the list
		for (int index = 0; index < attributes.size(); index++) {
			
			boolean add = true;
			for (int inner = 0; inner < subAttributes.size(); inner++) {
				if (attributes.get(index) == subAttributes.get(inner)) {
					add = false;
				}
			}
			if (add) {
				subAttributes.add(attributes.get(index));
			}
		}
		
		return subAttributes;
	}
	
	public ArrayList < SimpleCondition > getTreeConditions() {
		
		//Get the list of SimpleConditions from the condition of this select
		ArrayList < SimpleCondition > conditions;
		
		if (condition != null) {
			conditions = condition.getConditions();
		} else {
			conditions = new ArrayList < SimpleCondition > ();
		}
		
		//Get the SimpleConditions below this node
		ArrayList < SimpleCondition > subConditions = 
			tableOne.getTreeConditions();
		
		//Merge the two lists
		conditions.addAll(subConditions);
		
		//And return
		return conditions;
	}

	/**Says whether or not the Selection is a Leaf.  Always false, a Select is 
	 * never a Leaf in a query tree.
	 * @return <code<b>false</b></code> because select statements aren't leaves.
	 */
	public boolean isLeaf() {
		return false;
	}

	/**This method will set the value of condition.
	 * @param newCondition The new value of condition.
	 */
	public void setCondition(final Condition newCondition) {
		this.condition = newCondition;
	}
	
	public String toString() {
		
		String string = "|"; 
		
		string += this.queryID + "\t|";
		string += this.executionOrder + "\t|";
		string += this.type + "\t\t|";
		string += condition.toString() + "\t|";
		string += tableOneAccess +"\t|";
		string += resultTableID + "\t|";
		string += "\n";
		
		return string;
	}
	
	public long uniqueVals(String att){
		return tableOne.uniqueVals(att);
	}
}

