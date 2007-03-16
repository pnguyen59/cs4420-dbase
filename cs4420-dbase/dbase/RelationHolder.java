package dbase;

/**
 * @author gtg471h
 * 
 * This is a singleton class for holding the relation instances 
 * used by the different parts of the DBMS.
 *
 */

import java.util.*;

public class RelationHolder {
	
	/** The List of Relations */
	
	private static ArrayList<Relation> relations;
	
	/**
	 * Standard Constructor
	 *
	 */
	private RelationHolder() {
		relations = new ArrayList<Relation>();
	}
	
	/**
	 * The static get method, alays used.
	 * @return The static RelationHolder.
	 */
	public synchronized static RelationHolder getRelationHolder() {
		return new RelationHolder();
	}
	
	/**
	 * Gets a relation based on the Internal relation ID as defined in RELATION_CATALOG
	 * @param ID The Internal ID
	 * @return The denoted Relation
	 */
	
	public Relation getRelation(long ID) {
		for (int i = 0; i < relations.size(); i++) {
			if (relations.get(i).getID() == ID) {
				return relations.get(i);
			}
		}
		//TODO Possibly add code to load this Relation if not already done, we'll see.
		return null;
	}
	
	/**
	 * Adds a relation to the list.
	 * @param newrelation The new Relation to add.
	 * @return A boolean of whether or not the Relation was successfully added.
	 */
	public boolean addRelation(Relation newrelation) {
		return relations.add(newrelation);
	}
	
	/**
	 * This method is used by SystemCatalog to assign IDs
	 * @return the smallest unused relation ID
	 */
	public int getSmallestUnusedID(){
		int j=0;
		while (getRelation(j)!=null){
			j++;
		}
		return j;
	}
	
	/**
	 * 
	 * @param name The name of the relation to get
	 * @return the relation ID, or -1 if relation not found
	 */
	public int getRelationByName(String name){
		for (int j=0; j<relations.size(); j++){
			if (relations.get(j).getName().equals(name)){
				return relations.get(j).getID();
			}
		}
		return -1;
	}
	
	public String toString(){
		return relations.toString();
	}
	
	

}
