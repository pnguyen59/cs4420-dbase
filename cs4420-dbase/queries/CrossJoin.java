package queries;

public class CrossJoin extends Operation {

	protected int tableTwoID;
	
	protected int tableTwoAccess;
	
	@Override
	public long calculateCost() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String toString() {
		
		String string = ""; 
		
		string += this.queryID + "\t";
		string += this.executionOrder + "\t";
		string += this.type + "\t";
		string += "\t";
		string += tableOneID + "\t";
		string += tableTwoID + "\t";
		string += tableOneAccess +"\t";
		string += tableTwoAccess +"\t";
		string += "\t";
		string += resultTableID;
		
		return string;
	}


}
