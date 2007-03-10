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
	 * gets a relation based on the Internal relation ID as defined in RELATION_CATALOG
	 * @param ID The Internal ID
	 * @return The denoted Relation
	 */
	
	public Relation getRelation(int ID) {
		//TODO Add search function to search list for internal ID number
		return new Relation();
	}
	
	/**
	 * Adds a relation to the list.
	 * @param newrelation The new Relation to add.
	 * @return A boolean of whether or not the Relation was successfully added.
	 */
	public boolean addRelation(Relation newrelation) {
		return relations.add(newrelation);
	}
	
	

}
