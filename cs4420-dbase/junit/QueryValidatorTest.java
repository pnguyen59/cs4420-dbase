package junit;


import java.util.ArrayList;

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

	public static String selectFromSelece = "(PROJECT (qa \"A\") (SELECT (SELECT (CROSSJOIN \"A\", \"B\") (WHERE (EQ (a \"C\") (a \"D\"))))(WHERE(EQ (a \"E\") (a \"F\")))))";
	
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
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
	public void testValidateComparisonsInvalidComparisons() {
		
		//Add some relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		
		holder.addRelation(new Relation("WALRUS", 1));
		holder.addRelation(new Relation("CHICKEN", 2));
		holder.addRelation(new Relation("MOOSE", 3));
		
		Relation relation = holder.getRelation(1);
		relation.addAttribute(new Attribute("INT1", Type.Int, 1));
		relation.addAttribute(new Attribute("INT2", Type.Int, 2));
		relation.addAttribute(new Attribute("CHAR1,", Type.Char, 3));
		
		//Try it with comparing like attributes
		String queryString = "(PROJECT (a \"INT1\" a \"INT2\") "
			+ "(SELECT (\"WALRUS\") (WHERE (EQ (A \"INT1\") (A \"CHAR1\")))))";
		Query query = new Query(queryString);
		assertFalse(QueryValidator.validateConditions(query));
		
		//Try it with comparing an attribute and a constant
		 queryString = "(PROJECT (a \"INT1\" a \"INT2\") "
			+ "(SELECT (\"WALRUS\") (WHERE (EQ (A \"INT1\") (K CHAR \"A\")))))";
		 query = new Query(queryString);
			assertFalse(QueryValidator.validateConditions(query));
	}
	
	@Test
	public void testValidateComparisonsValidComparisons() {
		
		//Add some relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		
		holder.addRelation(new Relation("WALRUS", 1));
		holder.addRelation(new Relation("CHICKEN", 2));
		holder.addRelation(new Relation("MOOSE", 3));
		
		Relation relation = holder.getRelation(1);
		relation.addAttribute(new Attribute("INT1", Type.Int, 1));
		relation.addAttribute(new Attribute("INT2", Type.Int, 2));
		
		//Try it with comparing like attributes
		String queryString = "(PROJECT (a \"INT1\" a \"INT2\") "
			+ "(SELECT (\"WALRUS\") (WHERE (EQ (A \"INT1\") (A \"INT2\")))))";
		Query query = new Query(queryString);
		assertTrue(QueryValidator.validateConditions(query));
		
		//Try it with comparing an attribute and a constant
		 queryString = "(PROJECT (a \"INT1\" a \"INT2\") "
			+ "(SELECT (\"WALRUS\") (WHERE (EQ (A \"INT1\") (K INT 3)))))";
		 query = new Query(queryString);
			assertTrue(QueryValidator.validateConditions(query));
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
	public void testValidateQueryValidQuery() {
		
		//Add some relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		
		holder.addRelation(new Relation("TABLE_1", 100));
		holder.addRelation(new Relation("TABLE_2", 101));
		holder.addRelation(new Relation("TABLE_3", 102));
		
		//Add some attributes
		Relation relation = holder.getRelation(100);
		relation.addAttribute(new Attribute("TABLE_1_INT_1", Type.Int, 100));
		relation.addAttribute(new Attribute("TABLE_1_INT_2", Type.Int, 101));
		relation.addAttribute(new Attribute("SAME_NAME", Type.Int, 102));
		relation = holder.getRelation(101);
		relation.addAttribute(new Attribute("TABLE_2_INT_1", Type.Int, 103));
		relation.addAttribute(new Attribute("TABLE_2_INT_2", Type.Int, 104));
		relation.addAttribute(new Attribute("SAME_NAME", Type.Int, 105));
		relation = holder.getRelation(102);
		relation.addAttribute(new Attribute("TABLE_3_CHAR_1", Type.Int, 106));
		relation.addAttribute(new Attribute("TABLE_3_CHAR_2", Type.Int, 107));
		relation.addAttribute(new Attribute("SAME_NAME", Type.Char, 108));

		//Now try some valid queries
		
		//First a very simple, one attribute, one table
		String queryString = "(PROJECT (a \"TABLE_1_INT_1\") (\"TABLE_1\"))";
		Query query = new Query(queryString);
		assertTrue(QueryValidator.validateQuery(query));
		
		//Now try it with a qualified attribute
		queryString = "(PROJECT"
			+ "(qa \"TABLE_1\" \"SAME_NAME\", qa \"TABLE_2\" \"SAME_NAME\")"
			+ "(CROSSJOIN (\"TABLE_1\", \"TABLE_2\")))";
		query = new Query(queryString);
		assertTrue(QueryValidator.validateQuery(query));
		
		//Now try it with qualified attributes and a select
		queryString = "(PROJECT (qa \"TABLE_1\" \"SAME_NAME\", qa \"TABLE_2\" \"SAME_NAME\") (SELECT (CROSSJOIN (\"TABLE_1\", \"TABLE_2\")) (WHERE (EQ (A \"TABLE_1_INT_1\") (K int 3)))))";
		query = new Query(queryString);
		assertTrue(QueryValidator.validateQuery(query));
	}
	
	@Test
	public void testValidateQueryInvalidQuery() {
		//Add some relations
		RelationHolder holder = RelationHolder.getRelationHolder();
		
		holder.addRelation(new Relation("TABLE_1", 100));
		holder.addRelation(new Relation("TABLE_2", 101));
		holder.addRelation(new Relation("TABLE_3", 102));
		
		//Add some attributes
		Relation relation = holder.getRelation(100);
		relation.addAttribute(new Attribute("TABLE_1_INT_1", Type.Int, 100));
		relation.addAttribute(new Attribute("TABLE_1_INT_2", Type.Int, 101));
		relation.addAttribute(new Attribute("SAME_NAME", Type.Int, 102));
		relation = holder.getRelation(101);
		relation.addAttribute(new Attribute("TABLE_2_INT_1", Type.Int, 103));
		relation.addAttribute(new Attribute("TABLE_2_INT_2", Type.Int, 104));
		relation.addAttribute(new Attribute("SAME_NAME", Type.Int, 105));
		relation = holder.getRelation(102);
		relation.addAttribute(new Attribute("TABLE_3_CHAR_1", Type.Int, 106));
		relation.addAttribute(new Attribute("TABLE_3_CHAR_2", Type.Int, 107));
		relation.addAttribute(new Attribute("SAME_NAME", Type.Char, 108));

		//Now try some valid queries
		
		//First a very simple, one attribute, one table
		String queryString = "(PROJECT (a \"TABLE_2_INT_1\") (\"TABLE_1\"))";
		Query query = new Query(queryString);
		assertFalse(QueryValidator.validateQuery(query));
		
		//Now try it with what need to be qualified attribute
		queryString = "(PROJECT"
			+ "(a \"SAME_NAME\", qa \"TABLE_2\" \"SAME_NAME\")"
			+ "(CROSSJOIN (\"TABLE_1\", \"TABLE_2\")))";
		query = new Query(queryString);
		assertFalse(QueryValidator.validateQuery(query));
		
		//Now try it with qualified attributes and a select
		queryString = "(PROJECT (qa \"TABLE_1\" \"SAME_NAME\", qa \"TABLE_2\" \"SAME_NAME\") (SELECT (CROSSJOIN (\"TABLE_1\", \"TABLE_2\")) (WHERE (EQ (A \"TABLE_1_INT_1\") (K CHAR 3)))))";
		query = new Query(queryString);
		assertFalse(QueryValidator.validateQuery(query));
	}
	
	@Test
	public void testValidateRelationsValidRelations() {

		System.out.println();
		System.out.println("testValidateRelations");
		System.out.println();
		
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
		ArrayList < String > tables = query.getRelations(); 
		for (int index = 0; index < tables.size(); index++) {
			System.out.println("HAS TABLE:"  + tables.get(index));
		}
		assertTrue(QueryValidator.validateTables(query));
	}
	
	@Test
	public void testValidateQueryExamples() {
		
		System.out.println();
		System.out.println("testValidateQueryExamples");
		System.out.println();
		
		//EXAMPLE: (project (a ÒaÓ, a ÒbÓ, a ÒcÓ)(select (table ÒRÓ))
		//Add the relation
		RelationHolder holder = RelationHolder.getRelationHolder();	
		holder.addRelation(new Relation("R", 200));
		Relation relation = holder.getRelation(200);
		relation.addAttribute(new Attribute("a", Type.Int, 200));
		relation.addAttribute(new Attribute("b", Type.Int, 201));
		relation.addAttribute(new Attribute("c", Type.DateTime, 202));
		String queryString = "(project (a \"a\", a \"b\", a \"c\")(select (table \"R\"))";
		Query query = new Query(queryString);
		assertTrue(QueryValidator.validateQuery(query));
		ArrayList < String > tables = query.getRelations(); 
		
		//With a bigger one
		queryString = "(project (a \"a\") (select (table \"R\") (where (gt (a \"C\") (k dateTime \"03:21:2007 16:30:00\")))))";
		query = new Query(queryString);
		tables = query.getRelations(); 
		assertTrue(QueryValidator.validateQuery(query));
		query.printQueryTree();
	}
	
	@Test
	public void testValidateQuerySelectFromSelect() {
		
		System.out.println();
		System.out.println("testValidateQuerySelectFromSelect");
		System.out.println();
		
		RelationHolder holder = RelationHolder.getRelationHolder();	
		holder.addRelation(new Relation("A", 300));
		holder.addRelation(new Relation("B", 301));
		holder.addRelation(new Relation("C", 302));
		holder.addRelation(new Relation("D", 303));
		
		Relation relation = holder.getRelation(300);
		Attribute attribute = new Attribute("WOW", Type.Int, 300);
		attribute.setParent(300);
		relation.addAttribute(attribute);
		
		relation = holder.getRelation(301);
		attribute = new Attribute("WOW", Type.Int, 301);
		attribute.setParent(301);
		relation.addAttribute(attribute);
		
		relation = holder.getRelation(302);
		attribute = new Attribute("SOW", Type.Int, 302);
		attribute.setParent(302);
		relation.addAttribute(attribute);
		
		relation = holder.getRelation(303);
		attribute = new Attribute("POW", Type.Int, 303);
		attribute.setParent(303);
		relation.addAttribute(attribute);

		String queryString = "(PROJECT (qa \"A\" \"WOW\", qa \"B\" \"WOW\") (CROSSJOIN \"A\", \"B\", \"C\", \"D\"))";
		Query query = new Query(queryString);
		assertTrue(QueryValidator.validateQuery(query));
		query.printQueryTree();
		query.assignTemporaryTables();
		query.generateTemporaryTables();
		
		queryString = "(PROJECT (qa \"A\" \"WOW\", qa \"B\" \"WOW\", a \"SOW\") (CROSSJOIN \"A\", \"B\", \"C\", \"D\"))";
		query = new Query(queryString);
		assertTrue(QueryValidator.validateQuery(query));
		query.printQueryTree();
		query.assignTemporaryTables();
		query.generateTemporaryTables();
		
		queryString = "(PROJECT (qa \"A\" \"WOW\", qa \"B\" \"WOW\", a \"SOW\", a \"POW\") (CROSSJOIN \"A\", \"B\", \"C\", \"D\"))";
		query = new Query(queryString);
		assertTrue(QueryValidator.validateQuery(query));
		query.printQueryTree();
		query.assignTemporaryTables();
		query.generateTemporaryTables();
		
	}

}
