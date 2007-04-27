package queries;

import java.util.ArrayList;

import dbase.Relation;
import dbase.RelationHolder;

public class QueryValidator {

	
	public static boolean validateQuery(final Query query) {
		
		//See if the tables and attributes are valid
		if (!validateTables(query)) {
			return false;
		} else if (!validateAttributes(query)) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean validateTables(final Query query) {
		
		
		ArrayList < String > tables = query.getRelations();
		
		//For the list of tables, see that they exist
		RelationHolder holder = RelationHolder.getRelationHolder();
		
		for (int index = 0; index < tables.size(); index++) {
			int relationID = holder.getRelationByName(tables.get(index));
			Relation relation = holder.getRelation(relationID);
			if (relation == null) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean validateAttributes(final Query query) {
		
		//Get the list of table IDx
		ArrayList < String > tableIDs = query.getRelations();
		
		//Get the attributes in the query
		ArrayList < String > attributes = query.getAttributes();
		
		//Get the list of tables
		ArrayList < Relation > tables = new ArrayList < Relation > ();
		RelationHolder holder = RelationHolder.getRelationHolder();
		for (int index = 0; index < tableIDs.size(); index++) {
			int relationID = holder.getRelationByName(tableIDs.get(index));
			tables.add(holder.getRelation(relationID));
		}
		
		//See that one of the relations has each of the attributes
		for (int index = 0; index < attributes.size(); index++) {
			
			String attributeName = attributes.get(index);
			
			ArrayList < Relation > containing = new ArrayList < Relation > ();
			
			//Go through all of the tables to see which, if any containt the
			//attribute
			for (int inner = 0; inner < tables.size(); inner++) {
				
				Relation relation = tables.get(inner);
				if (relation.hasAttributeWithName(attributeName)) {
					containing.add(relation);
				}
				
				//See if more than one relation has this attribute
				if (containing.size() > 1) { 
					System.out.println("More than one relation has " 
						+ attributeName);
					return false;
				} else if (containing.size() == 0) {
					System.out.println("No relation has "
						+ attributeName);
					return false;
				}
				
			}
			
		}
		
		return true;
	}
	
	public static boolean validateConditions(final Query query) {
		
		ArrayList < Condition > comparisons = query.getConditions();
		
		//Get the list of table IDx
		ArrayList < String > tableIDs = query.getRelations();

		//Get the attributes in the query
		ArrayList < String > attributes = query.getAttributes();

		//Get the list of tables
		ArrayList < Relation > tables = new ArrayList < Relation > ();
		RelationHolder holder = RelationHolder.getRelationHolder();
		for (int index = 0; index < tableIDs.size(); index++) {
			int relationID = holder.getRelationByName(tableIDs.get(index));
			tables.add(holder.getRelation(relationID));
		}	
			
		return false;	
	}
	
	
	
}
