package queries;

import java.util.ArrayList;

import dbase.Attribute;
import dbase.Database;
import dbase.Iterator;
import dbase.Relation;
import dbase.RelationHolder;

public class Project extends Operation {

	protected ArrayList < String > attributes;
	
	/**This will create a new instance of Project.  It will find what to 
	 * project and from where from the statement passed.
	 * @param statement The literal of the project statement.
	 */
	public Project (final String statement) {
		
		setType(QueryParser.PROJECT);
		
		ArrayList < String > parts = QueryParser.parseStatementParts(statement);
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
		return tableOne.calculateCost();
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
	
	public boolean execute(){
		if (!tableOne.execute()){
			return false;
		}
		Relation r = RelationHolder.getRelationHolder().getRelation(this.resultTableID);
		Relation s = RelationHolder.getRelationHolder().getRelation(tableOne.getResultTableID());
		String attnames = "";
		if (r == null) return false;
		for (String st: attributes){
			Attribute att = s.getAttributeByName(st.trim());
			r.addAttribute(att.getName(), att.getType(), Database.getCatalog().getSmallestUnusedAttributeID());
			attnames += st + ", ";
		}
		attnames = attnames.substring(0, attnames.length()-2);
		Iterator it = new Iterator (s);
		String newattvals = "";
		while (it.hasNext()){
			String [] oldattvals = it.getNext();
			for (int j=0; j<attributes.size(); j++){
				int idx = s.getIndexByName(attributes.get(j));
				newattvals += oldattvals[idx] + ", ";
			}
			newattvals = newattvals.substring(0, newattvals.length()-2);
			if (!Database.getCatalog().insert(this.resultTableID, "("+newattvals+")") )return false;
		}
		return true;
		
		
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
	
	public ArrayList < String > getTreeAttributes() {
		
		//Merge the list of attributes from this one and those below it
		ArrayList < String > subAttributes = tableOne.getTreeAttributes();
		
		//Merge and return the list
		for (int index = 0; index < attributes.size(); index++) {
			
			boolean add = true;
			for (int inner = 0; inner < subAttributes.size(); inner++) {
				if (attributes.get(index).
					equalsIgnoreCase(subAttributes.get(inner))) {
					add = false;
				}
			}
			if (add) {
				subAttributes.add(attributes.get(index));
			}
		}
		return subAttributes;
	}
	
	/**The project doesn't really have any conditions associated with it, but
	 * suff below it might.  You know, just in case it does.
	 * @return All of the SimpleConditions in the tree below this project.
	 */
	public ArrayList < SimpleCondition > getTreeConditions() {
	
		return tableOne.getTreeConditions();
		
	}
	
}
