package junit;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import queries.Query;
import queries.QueryValidator;
import dbase.Attribute;
import dbase.Relation;
import dbase.RelationHolder;
import dbase.Attribute.Type;

public class QueryValidatorTest extends TestCase {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}
	
	@Test
	public void testValidateRelationsValidRelations() {
		
		//Add some relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		
		holder.addRelation(new Relation("WALRUS", 1));
		holder.addRelation(new Relation("CHICKEN", 2));
		holder.addRelation(new Relation("MOOSE", 3));
		
		//See if the query is valid
		String queryString = "(PROJECT (a \"a\") (\"WALRUS\"))";
		
		Query query = new Query(queryString);
		
		assertTrue(QueryValidator.validateTables(query));
		
		queryString = "(PROJECT (a \"a\") (CROSSJOIN (\"WALRUS\", \"CHICKEN\")))";
		query = new Query(queryString);
		assertTrue(QueryValidator.validateTables(query));
	}
	
	@Test
	public void testValidateRelationsInvalidRelations() {
		
		//Add some relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		
		holder.addRelation(new Relation("WALRUS", 1));
		holder.addRelation(new Relation("CHICKEN", 2));
		holder.addRelation(new Relation("MOOSE", 3));
		
		//See if the query is valid
		String queryString = "(PROJECT (a \"a\") (\"POOP\"))";
		
		Query query = new Query(queryString);
		
		assertFalse(QueryValidator.validateTables(query));
		
		queryString = "(PROJECT (a \"a\") (CROSSJOIN (\"POOP\", \"COW\")))";
		query = new Query(queryString);
		assertFalse(QueryValidator.validateTables(query));
	}
	
	@Test
	public void testValidateAttributesValidAttributes() {
		
		//Add some relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		
		holder.addRelation(new Relation("WALRUS", 1));
		holder.addRelation(new Relation("CHICKEN", 2));
		holder.addRelation(new Relation("MOOSE", 3));
		
		Relation relation = holder.getRelation(1);
		relation.addAttribute(new Attribute("INT1", Type.Int, 1));
		relation.addAttribute(new Attribute("INT2", Type.Int, 2));
		
		String queryString = "(PROJECT (a \"INT1\") (\"WALRUS\"))";
		Query query = new Query(queryString);
		assertTrue(QueryValidator.validateAttributes(query));
		
		queryString = "(PROJECT (a \"INT1\" a \"INT2\") (\"WALRUS\"))";
		query = new Query(queryString);
		assertTrue(QueryValidator.validateAttributes(query));
		
		queryString = 
			"(PROJECT (qa \"WALRUS\" \"INT1\" qa \"WALRUS\" \"INT2\") (CROSSJOIN (\"WALRUS\", \"CHICKEN\")))";
		query = new Query(queryString);
		assertTrue(QueryValidator.validateAttributes(query));
	}
	
	@Test
	public void testValidateAttributesInvalidAttributes() {
		
		//Add some relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		
		holder.addRelation(new Relation("WALRUS", 1));
		holder.addRelation(new Relation("CHICKEN", 2));
		holder.addRelation(new Relation("MOOSE", 3));
		
		Relation relation = holder.getRelation(1);
		relation.addAttribute(new Attribute("INT1", Type.Int, 1));
		relation.addAttribute(new Attribute("INT2", Type.Int, 2));
		
		relation = holder.getRelation(2);
		relation.addAttribute(new Attribute("INT1", Type.Int, 1));
		relation.addAttribute(new Attribute("INT2", Type.Int, 2));
		
		String queryString = "(PROJECT (a \"INT3\") (\"WALRUS\"))";
		Query query = new Query(queryString);
		assertFalse(QueryValidator.validateAttributes(query));
		
		queryString = "(PROJECT (a \"INT3\" a \"INT4\") (\"WALRUS\"))";
		query = new Query(queryString);
		assertFalse(QueryValidator.validateAttributes(query));
		
		queryString = 
			"(PROJECT (a \"INT3\" a \"INT4\") (CROSSJOIN (\"WALRUS\", \"CHICKEN\")))";
		query = new Query(queryString);
		assertFalse(QueryValidator.validateAttributes(query));
		
		queryString = "(PROJECT (a \"INT3\" a \"INT4\") (\"WALRUS\"))";
		query = new Query(queryString);
		assertFalse(QueryValidator.validateAttributes(query));
		
		queryString = 
			"(PROJECT (a \"INT1\" a \"INT2\") (CROSSJOIN (\"WALRUS\", \"CHICKEN\")))";
		query = new Query(queryString);
		assertFalse(QueryValidator.validateAttributes(query));
	}

}
