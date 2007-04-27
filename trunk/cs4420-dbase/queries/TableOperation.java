package queries;

import java.util.ArrayList;

import dbase.Attribute;
import dbase.Relation;
import dbase.RelationHolder;

public class TableOperation extends Operation {

	private String tableName;
	
	/**This will create a new instance of TableOperation.  It will used the 
	 * thingy sent to represent the name of the table, Relation, or whatever
	 * you want to call it to name itself.
	 * @param newTableName  The name of the table this will represent fo
	 * purposes of the query tree.
	 */
	public TableOperation(final String newTableName) {
		type = QueryParser.TABLEOPERATION;
		
		//System.out.println("TABLE: "  + newTableName);
		
		//Find the table name
		int open = newTableName.indexOf('\"');
		int close;
		for (close = open + 1; close < newTableName.length(); close++) {
			//Get the character
			char currentCharacter = newTableName.charAt(close);
			if (currentCharacter == '\"') {
				break;
			}	
		}
		
		//Set the table name from open + 1 to the close
		this.setTableName(newTableName.substring(open + 1, close));
		//System.out.println("TABLE: "  + tableName);
		this.resultTableID = RelationHolder.getRelationHolder().getRelationByName(tableName);
	}
	
	/**This method will calculate the cost, that is the size of this
	 * <code>TableOperation</code>. Basically, it's just the size of the
	 * relation.
	 * @return The cost of this operation, essentially the cost of reading
	 * the whole thing in, in blocks.  -1 if relation name doesn't exist.  
	 * -2 if relation ID doesn't give us a relation
	 */
	@Override
	public long calculateCost() {
		
		//Find the relation that this TableOperation uses
		RelationHolder holder = RelationHolder.getRelationHolder();
		int relationID = holder.getRelationByName(tableName);
		
		//check for error condition
		if (relationID == -1) return -1;
		Relation relation = holder.getRelation(relationID);
		
		//if relation doesn't exist
		if (relation == null) return -2;
		
		//Find out how big this thing is
		int blocks = relation.getBlocktotal();
		
		
		return blocks;
	}
	
	public boolean containsAttribute(String att){
		RelationHolder holder = RelationHolder.getRelationHolder();
		int relationID = holder.getRelationByName(tableName);
		
		//check for error condition
		if (relationID == -1) return false;
		Relation relation = holder.getRelation(relationID);
		
		//if relation doesn't exist
		if (relation == null) return false;
		if (Utilities.isQualifiedAttr(att)){
			if (relation.getName().equals(att.split(".")[0])){
				return (relation.getAttributeByName(att.split(".")[1]) != null);
			} else{
				return false;
			}
		} else {
			return (relation.getAttributeByName(att) != null);
		}
	}
	
	public ArrayList<String> getAttributes(){
		RelationHolder holder = RelationHolder.getRelationHolder();
		int relationID = holder.getRelationByName(tableName);
		
		//check for error condition
		if (relationID == -1) return null;
		Relation relation = holder.getRelation(relationID);
		
		//if relation doesn't exist
		if (relation == null) return null;
		
		ArrayList <Attribute> attr = relation.getAttributes();
		ArrayList <String> ret = new ArrayList <String>();
		for (int j=0; j<attr.size(); j++){
			ret.add(relation.getName()+"."+attr.get(j).getName());
		}
		return ret;
	}
	
	public boolean execute(){
		return true;
	}

	/**Tables will be the leaves of the query tree and never have children.
	 * @return <code><b>false</b></code> because leaves never have children.
	 */
	public boolean getAllowsChildren() {
		return false;
	}

	/**Tables will be the leaves of the query tree and never have children.
	 * @return 0
	 */
	public int getChildCount() {
		return 0;
	}

	public ArrayList < String > getRelations() {
		ArrayList < String > relationList = new ArrayList < String > ();
		relationList.add(tableName);
		return relationList;
	}

	/**This method returns the value of tableName.
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	
	public ArrayList < String > getTreeAttributes() {
		
		return new ArrayList < String > ();
	}

	/**For the TableOperation, there will never be any conditions, nor will
	 * there be any leaves.  For this reason we return an empty list
	 * of SimpleConditons.
	 * @return An empty list of SimpleConditions.
	 */
	public ArrayList < SimpleCondition > getTreeConditions() {
		return new ArrayList < SimpleCondition > ();
	}
	
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return true;
	}
	
	/**This method will see if this thingy is valid.  In this case, that really
	 * just means that the table exists.
	 * @return Whether or not this <code>TableOperaion</code> is valid.
	 */
	public boolean isValid() {
		RelationHolder holder = RelationHolder.getRelationHolder();
		int relationID = holder.getRelationByName(tableName);
		return(holder.getRelation(relationID) != null);
	}
	
	/**This method will set the value of tableName.
	 * @param newTableName The new value of tableName.
	 */
	public void setTableName(final String newTableName) {
		this.tableName = newTableName;
	}
	
	public String toString() {
		return "";
	}

	public long uniqueVals(String att){
//		Find the relation that this TableOperation uses
		RelationHolder holder = RelationHolder.getRelationHolder();
		int relationID = holder.getRelationByName(tableName);
		
		//check for error condition
		if (relationID == -1) return -1;
		Relation relation = holder.getRelation(relationID);
		
		//if relation doesn't exist
		if (relation == null) return -2;
		
		long uniquevals = relation.getUniqueVals(att);
		
		return uniquevals;
	}
}
