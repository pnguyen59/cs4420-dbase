package queries;

import java.util.ArrayList;

import dbase.Attribute;

public class Project extends Operation {

	protected ArrayList < Attribute > attributes;
	
	public long calculateCost() {
		return 1000;
	}
	
	public String toString() {
		
		String string = ""; 
		
		string += this.queryID + "\t";
		string += this.executionOrder + "\t";
		string += this.type + "\t";
		for (int index = 0; index < attributes.size(); index++) {
			string += attributes.get(index).getName() + ", ";
		}
		string += tableOneID + "\t\t";
		string += tableOneAccess +"\t\t";
		string += "\t";
		string += resultTableID;
		
		return string;
	}
	
}
