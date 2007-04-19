package queries;
import java.util.ArrayList;

public class Join extends Operation {

	protected ArrayList < Condition > conditions;
	
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
		for (int index = 0; index < conditions.size(); index++) {
			string += conditions.get(index).toString() + ", ";
		}
		string += tableOneID + "\t";
		string += tableTwoID + "\t";
		string += tableOneAccess +"\t";
		string += tableTwoAccess +"\t";
		string += "\t";
		string += resultTableID;
		
		return string;
	}

}
