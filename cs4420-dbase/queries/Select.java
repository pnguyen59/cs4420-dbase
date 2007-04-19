package queries;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;


public class Select extends Operation {

	protected ArrayList < Condition > conditions;
	
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
			string += conditions.toString() + ", ";
		}
		string += tableOneID + "\t\t";
		string += tableOneAccess +"\t\t";
		string += "\t";
		string += resultTableID;
		
		return string;
	}	

}
