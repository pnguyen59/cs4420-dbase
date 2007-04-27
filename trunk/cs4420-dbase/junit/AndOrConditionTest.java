package junit;

import static org.junit.Assert.fail;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import queries.AndOrCondition;
import queries.Condition;

public class AndOrConditionTest extends TestCase{

	public static String simpleAndTest = 
		"(AND (eq \"a\" \"b\") (eq \"c\" \"d\"))"; 
	
	public static String complexAndTest = 
		"(AND (AND (eq \"a\" \"b\") (eq \"c\" 1)) (eq \"a\" \"b\"))";
	
	public static String veryComplexAndTest = 
		"(and (eq (qa \"R\" \"c\") (qa \"S\" \"c\")) (or"
		+ "(eq (a \"a\") (k string \"leo\") )(lt (a \"e\") (k int 4) )))";
	
	public static String anotherAndTest = 
		"(and(eq (qa \"P\" \"e\") (qa \"Q\" \"f\"))"
		+ "(eq  (qa \"Q\" \"g\") (qa \"R\" \"h\")))";
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCompare() {
		String tupvals[] = new String[]{"a","a","b","b"};
		String tupnames[]= new String[]{"P.e","Q.f","Q.g","R.h"};
		String tuptypes[]= new String[]{"string", "string", "string", "string"};
		Condition cond = Condition.makeCondition(anotherAndTest);
		assertTrue(cond.compare(tupnames,tupvals,tuptypes));
		
	}

	@Test
	public void testGetAttributes() {
	}

	@Test
	public void testGetRelations() {
	}

	@Test
	public void testParseConditons() {
		
		String [] conditions;
		
		conditions = AndOrCondition.parseConditons(simpleAndTest);
		
		for (int index = 0; index < conditions.length; index++) {
			System.out.println(conditions[index]);
		}
		
		conditions = AndOrCondition.parseConditons(complexAndTest);
		
		for (int index = 0; index < conditions.length; index++) {
			System.out.println(conditions[index]);
		}
		
		conditions = AndOrCondition.parseConditons(veryComplexAndTest);
		
		for (int index = 0; index < conditions.length; index++) {
			System.out.println(conditions[index]);
		}
		
		conditions = AndOrCondition.parseConditons(anotherAndTest);
		
		for (int index = 0; index < conditions.length; index++) {
			System.out.println(conditions[index]);
		}
		
	}

	@Test
	public void testParseLeftHand() {
	}

	@Test
	public void testParseRightHand() {
	}

	@Test
	public void testAndOrCondition() {
	}

}
