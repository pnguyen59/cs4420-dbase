package queries;

import java.util.ArrayList;

import dbase.Relation;
import dbase.RelationHolder;

public class SimpleCondition extends Condition {
	
	public SimpleCondition(final String condition, final int relation) {
		super(condition, relation);
		
		//Find the attributes given the relation
		Relation currentRelation = RelationHolder.
			getRelationHolder().getRelation(relation);
	}
	
	@Override
	public boolean compare(final String tuple) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList < Integer > getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

}
