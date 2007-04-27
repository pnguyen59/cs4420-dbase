package junit;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import queries.CrossJoin;
import queries.TableOperation;

public class CrossJoinTest extends TestCase {

	CrossJoin join;
	
	public static String twoTables = "(CROSSJOIN \"A\", \"B\")";
	
	public static String threeTables = "(CROSSJOIN \"A\", \"B\", \"C\")";
	
	public static String manyTables = "(CROSSJOIN \"A\", \"B\", \"C\", \"D\")";
	
	
	
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
	public void testCrossJoinManyTables() {
	}
	
	@Test
	public void testCrossJoinThreeTables() {
		
		System.out.println();
		System.out.println("testCrossJoinThreeTables");
		System.out.println();
		
		join = new CrossJoin(threeTables);
		
		assertTrue("Should have 4 children, but have " 
			+ join.getChildCount(), join.getChildCount() == 4);	
		
		//See that table one is A
		assertTrue("Table 1 should be A, but was "
			+ ((TableOperation)join.getTableOne()).getTableName(),
			((TableOperation)join.getTableOne()).getTableName().
			equalsIgnoreCase("A"));
		
		//Get the first inner join and see that its tableOne is B
		join = (CrossJoin) join.getTableTwo();
		assertTrue("Table 1 should be B, but was "
				+ ((TableOperation)join.getTableOne()).getTableName(),
				((TableOperation)join.getTableOne()).getTableName().
				equalsIgnoreCase("B"));
	}
	
	@Test
	public void testCrossJoingTwoTables() {
		
		System.out.println();
		System.out.println("testCrossJoinThreeTables");
		System.out.println();
		
		join = new CrossJoin(twoTables);
		
		assertTrue("Should have two children, but have " 
			+ join.getChildCount(), join.getChildCount() == 2);
		
		//See that table one is A
		assertTrue("Table 1 should be A, but was "
			+ ((TableOperation)join.getTableOne()).getTableName(),
			((TableOperation)join.getTableOne()).getTableName().
			equalsIgnoreCase("A"));
		assertTrue("Table 2 should be B, but was "
				+ ((TableOperation)join.getTableTwo()).getTableName(),
				((TableOperation)join.getTableTwo()).getTableName().
				equalsIgnoreCase("B"));
	}
	
	@Test 
	public void testGetRelations() {
		
		System.out.println();
		System.out.println("testCrossJoinThreeTables");
		System.out.println();
		
		join = new CrossJoin(twoTables);
		
		ArrayList < String > tables = join.getRelations();
		
		assertTrue("Table should be A, was "
			+ tables.get(0), tables.get(0).equalsIgnoreCase("A"));
		assertTrue("Table should be B, was "
				+ tables.get(1), tables.get(1).equalsIgnoreCase("B"));
		
		
	}

}
