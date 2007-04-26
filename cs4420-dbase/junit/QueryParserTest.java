package junit;


import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import queries.QueryParser;


public class QueryParserTest extends TestCase{

	
	public static final String relationNames = "\"A\", \"B\", \"C\"";
	
	public static final String crossJoin = "(CROSSJOIN ";
	
	public static final String simpleAttributeList =
		"(a \"A\", a \"B\", a \"C\")";
	
	public static final String complexAttributeList =
		"(qa \"R\" \"A\", qa \"S\" \"B\", qa \"T\" \"C\")";
	
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test 
	public void testParseRelationNames() {
	
		String crossJoinStatement = crossJoin + relationNames + ")";
		
		ArrayList < String > results = QueryParser.parseRelationNames(
			crossJoinStatement);
		
		
		for (int index = 0; index < results.size(); index++) {
			System.out.println(results.get(index));
		}
		
		assertTrue(results.get(0).equalsIgnoreCase("A"));
		assertTrue(results.get(1).equalsIgnoreCase("B"));
		assertTrue(results.get(2).equalsIgnoreCase("C"));
	}
	
	@Test 
	public void testParseAttributeNamesSimple() {
		
		System.out.println();
		System.out.println("testParseAttributeNamesSimple");
		System.out.println();
		
		ArrayList < String > results = QueryParser.parseQueryAttributes(
			simpleAttributeList);
		assertTrue(results.get(0).equalsIgnoreCase("A"));
		assertTrue(results.get(1).equalsIgnoreCase("B"));
		assertTrue(results.get(2).equalsIgnoreCase("C"));
		
		
	}
	
	@Test 
	public void testParseAttributeNamesComplex() {
		
		System.out.println();
		System.out.println("testParseAttributeNamesComplex");
		System.out.println();
		
		ArrayList < String > results = QueryParser.parseQueryAttributes(
			complexAttributeList);
		
		assertTrue(results.get(0).equalsIgnoreCase("R.A"));
		assertTrue(results.get(1).equalsIgnoreCase("S.B"));
		assertTrue(results.get(2).equalsIgnoreCase("T.C"));
		
		
	}
	
	@Test
	public void testParseQuery() {
		
		QueryParser parser = new QueryParser();
		ArrayList < String > tokens = parser.parseQuery(
			"(project (a “a”, qa “R” “c”, a “e”)(select(crossJoin “R”, “S” )"
			+ "(where (and(eq  (qa “R” “c”) (qa “S” “c”))(or(eq (a “a”) (k string"
			+ " “leo”) )(lt (a “e”) (k int 4) ))))))");
		for (int index = 0; index < tokens.size(); index++) {
			System.out.println(tokens.get(index));
		}		
	}
	
	@Test
	public void testFormQueryTree() {
		
		QueryParser parser = new QueryParser();

		System.out.println(parser.formQueryTree(
			"(project (a “a”, qa “R” “c”, a “e”)(select(crossJoin “R”, “S” )"
			+ "(where (and(eq  (qa “R” “c”) (qa “S” “c”))(or(eq (a “a”) (k string"
			+ " “leo”) )(lt (a “e”) (k int 4) ))))))"));
	}
	
	

}
