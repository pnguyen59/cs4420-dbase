package junit;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import queries.Operation;
import queries.Project;
import queries.Select;
import queries.SimpleCondition;
import queries.TableOperation;

public class ProjectTest extends TestCase {

	public static String simpleAttributes = "(qa \"R\"\"A\", a \"B\", a \"C\")";
	
	public static String table = "(\"A_TABLE\")";
	
	public static String project = "(PROJECT ";
	
	public static String oneLevelProject = "(PROJECT (a \"A\") (\"A\"))";
	
	public static String simpleCrossJoin = "(CROSSJOIN \"A\", \"B\")";
	
	public static String simpleWhere = "(WHERE (EQ (A \"A\") (A \"B\")))";
	
	public static String simpleSelect = "(SELECT" + simpleCrossJoin
	+ simpleWhere + ")";
	
	public static String simpleTableProject = project + simpleAttributes
		+ table + ")";
	
	public Project projection;
	
	public static String simpleProjectSelect = project + simpleAttributes 
		+ simpleSelect + ")";
	
	public static final String crazyQuery = "(project (a \"a\", qa \"R\" \"c\","
		+ "a \"e\")(select(crossJoin \"R\", \"S\" )(where(and(eq  "
		+ "(qa \"R\" \"c\") (qa \"S\" \"c\"))"
		+ "(or(eq (a \"a\") (k string \"leo\") )(lt (a \"e\") (k int 4) ))))))";

	
	
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testCalculateCost() {
	}

	@Test
	public void testProjectFromTable() {
		
		System.out.println();
		System.out.println("testProjectFromTable");
		System.out.println();
		
		projection = (Project) Operation.makeOperation(simpleTableProject);
		
		assertTrue("Projection should have one child, but had "
			+ projection.getChildCount(), projection.getChildCount() == 1);
		
		TableOperation table = (TableOperation) projection.getTableOne();
		assertTrue("The table should be A_TABLE, was "
			+ table.getTableName(), table.getTableName().equalsIgnoreCase("A_TABLE"));
	
		ArrayList < String > results = projection.getAttributes();
		assertTrue("Attribute should have been R.A, was " 
			+ results.get(0), results.get(0).equalsIgnoreCase("R.A"));
		assertTrue("Attribute should have been B, was " 
				+ results.get(1), results.get(1).equalsIgnoreCase("B"));
		assertTrue("Attribute should have been C, was " 
				+ results.get(2), results.get(2).equalsIgnoreCase("C"));
		
	}
	
	public void testProjectFromSelect() {
		
		System.out.println();
		System.out.println("testProjectFromSelect");
		System.out.println();
		
		projection = (Project) Operation.makeOperation(simpleProjectSelect);
		
		assertTrue("Projection should have one child, but had "
			+ projection.getChildCount(), projection.getChildCount() == 4);
		
		Select table = (Select) projection.getTableOne();
		assertTrue("The table should have 3 children, had "
			+ table.getChildCount(), 
			table.getChildCount() == 3);
		
		System.out.println(Operation.generateQueryTable(projection));
		
		projection = (Project) Operation.makeOperation(crazyQuery);
		System.out.println(Operation.generateQueryTable(projection));
		
		
	}

	@Test
	public void testGetChildCount() {
	}
	
	@Test 
	public void testGetRelations() {
		System.out.println();
		System.out.println("testGetRelations");
		System.out.println();
		
		projection = (Project) Operation.makeOperation(oneLevelProject);
		
		ArrayList < String > results = projection.getRelations();
		
		assertTrue("Table should have been A, was "
			+ results.get(0), results.get(0).equalsIgnoreCase("A")); 
		
		projection = (Project) Operation.makeOperation(simpleProjectSelect);
		results = projection.getRelations();
		assertTrue("Table should have been A, was "
				+ results.get(0), results.get(0).equalsIgnoreCase("A"));
		assertTrue("Table should have been B, was "
				+ results.get(1), results.get(1).equalsIgnoreCase("B"));
		
		
		String queryString = "(PROJECT (a \"a\") (CROSSJOIN (\"WALRUS\", \"CHICKEN\")))";
		projection = (Project) Operation.makeOperation(queryString);
		ArrayList < String > tables = projection.getRelations();
		assertTrue("Table should be WARLRUS, was "
				+ tables.get(0), tables.get(0).equalsIgnoreCase("WALRUS"));
		assertTrue("Table should be CHICKEN, was "
				+ tables.get(1), tables.get(1).equalsIgnoreCase("CHICKEN"));
	}
	
	@Test 
	public void testGetTreeAttributes() {
		System.out.println();
		System.out.println("testGetTreeAttributes");
		System.out.println();
		
		projection = (Project) Operation.makeOperation(oneLevelProject);
		
		ArrayList < String > results = projection.getTreeAttributes();
		
		assertTrue("Attribute should have been A, was "
			+ results.get(0), results.get(0).equalsIgnoreCase("A"));
		
		projection = (Project) Operation.makeOperation(crazyQuery);
		results = projection.getTreeAttributes();
		
		for (int index = 0; index < results.size(); index++) {
			System.out.println("RESULT: " + results.get(index));
		}
		
		assertTrue("Attribute should have been R.C, was "
				+ results.get(0), results.get(0).equalsIgnoreCase("R.C"));
		assertTrue("Attribute should have been S.C, was "
				+ results.get(1), results.get(1).equalsIgnoreCase("S.C"));
		assertTrue("Attribute should have been A, was "
				+ results.get(2), results.get(2).equalsIgnoreCase("A"));
		assertTrue("Attribute should have been E, was "
				+ results.get(3), results.get(3).equalsIgnoreCase("E"));
		
	}
	
	@Test public void testGetTreeConditions() {
	
		System.out.println();
		System.out.println("testGetTreeConditionsNoConditions");
		System.out.println();
		
		//Give it a thingy with no conditions.  The result should be an empty
		//list
		projection = (Project) Operation.makeOperation(oneLevelProject);
		ArrayList < SimpleCondition > results = projection.getTreeConditions();
		
		//See that it is size 0
		assertTrue("There should be no conditions, but there were" 
			+ results.size(), results.size() == 0);
		
		//Try it with a simple condition
		projection = (Project) Operation.makeOperation(simpleProjectSelect);
		results = projection.getTreeConditions();
		//See that there is one condition
		assertTrue("There should be 1 condition, but there were" 
			+ results.size(), results.size() == 1);
		
		//Try it with the crazy query
		projection = (Project) Operation.makeOperation(crazyQuery);
		results = projection.getTreeConditions();
		//See that there are 3 conditions		
		assertTrue("There should be 3 conditions, but there were" 
			+ results.size(), results.size() == 3);
	}

}
