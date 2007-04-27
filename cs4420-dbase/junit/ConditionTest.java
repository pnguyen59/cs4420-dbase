package junit;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import queries.Condition;
import queries.SimpleCondition;
import queries.Utilities;

/**
 * 
 * @author dkitch
 *
 */
public class ConditionTest extends TestCase{
	private static final String simpleconditiontrue = "(EQ (k int 1) (k int 1))";
	private static final String simpleconditionfalse = "(EQ (k int 1) (k int 2))";
	
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEquals() {
		/*Condition cond = Condition.makeCondition(simpleconditiontrue);
		System.out.println();
		assertTrue("Error: makeCondition supposed to return SimpleCondition, returns "+cond.getClass().getSimpleName()+" instead", 
				cond.getClass().getSimpleName().equals("SimpleCondition"));
		assertTrue("Supposed to be true", cond.compare(new String[] {"a"}, new String[]{"1"}, new String[]{"int"}));
		
		cond = Condition.makeCondition(simpleconditionfalse);
		System.out.println();
		assertTrue("Error: makeCondition supposed to return SimpleCondition, returns "+cond.getClass().getSimpleName()+" instead", 
				cond.getClass().getSimpleName().equals("SimpleCondition"));
		assertFalse("1=2 supposed to return false",cond.compare(new String[] {"a"}, new String[]{"1"}, new String[]{"int"}));
		*/
		
		Condition cond;
		
		for (int j=0; j<10; j++){
			for (char c='A'; c<='C'; c++){
				String cond1 = "(EQ (a "+c+") (k int "+j+"))";
				for (int k=0; k<10; k++){
					String[] tupnames = new String[]{"A","B","C"};
					String[] tupvals;
					String[] tuptypes = new String[]{"int","int","int"};
					if (c=='A'){
						tupvals = new String[]{""+k,"88","99"};
					} else if (c=='B'){
						tupvals = new String[]{"77", ""+k,"99"};
					} else {
						tupvals = new String[]{"77", "88", ""+k};
					}
					cond = Condition.makeCondition(cond1);
					//assertTrue(((SimpleCondition)cond).getLeftHand().replace("(", "").replace(")", ""),((SimpleCondition)cond).getLeftHand().replace("(", "").replace(")", "").equals(""+c));
					if (j==k){
						assertTrue("j=k, supposed to be true "+Utilities.printArray(tupnames),cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("j!=k, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
				}
			}
		}
		
		for (int j=0; j<10; j++){
			
				String cond1 = "(EQ (a A) (a B))";
				String cond2 = "(EQ (a B) (a A))";
				String cond3 = "(EQ (a C) (a B))";
				String cond4 = "(EQ (a B) (a C))";
				String cond5 = "(EQ (a A) (a C))";
				String cond6 = "(EQ (a C) (a A))";
				
				for (int k=0; k<10; k++){
					for (int l=0; l<10; l++){
					String[] tupnames = new String[]{"A","B","C"};
					String[] tupvals = new String[]{""+j,""+k,""+l};
					String[] tuptypes = new String[]{"int","int","int"};
					cond = Condition.makeCondition(cond1);
					if (j==k){
						assertTrue("j=k, supposed to be true "+Utilities.printArray(tupnames),cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("j!=k, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
					
					cond = Condition.makeCondition(cond2);
					if (j==k){
						assertTrue("j=k, supposed to be true",cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("j!=k, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
					
					cond = Condition.makeCondition(cond3);
					if (l==k){
						assertTrue("l=k, supposed to be true "+Utilities.printArray(tupnames),cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("l!=k, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
					
					cond = Condition.makeCondition(cond4);
					if (l==k){
						assertTrue("l=k, supposed to be true",cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("l!=k, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
					
					cond = Condition.makeCondition(cond5);
					if (j==l){
						assertTrue("j=l, supposed to be true "+Utilities.printArray(tupnames),cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("j!=l, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
					
					cond = Condition.makeCondition(cond6);
					if (j==l){
						assertTrue("j=l, supposed to be true",cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("j!=l, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
				}
				}
			
		}
		
		
		
		
		
	}
	
	@Test
	public void testLessThan() {
		Condition cond = Condition.makeCondition(simpleconditiontrue);
		
		for (int j=0; j<10; j++){
			
				String cond1 = "(LT (a A) (a B))";
				String cond2 = "(LT (a B) (a A))";
				String cond3 = "(LT (a C) (a B))";
				String cond4 = "(LT (a B) (a C))";
				String cond5 = "(LT (a A) (a C))";
				String cond6 = "(LT (a C) (a A))";
				
				for (int k=0; k<10; k++){
					for (int l=0; l<10; l++){
					String[] tupnames = new String[]{"A","B","C"};
					String[] tupvals = new String[]{""+j,""+k,""+l};
					String[] tuptypes = new String[]{"int","int","int"};
					cond = Condition.makeCondition(cond1);
					if (j<k){
						assertTrue("j<k, supposed to be true "+Utilities.printArray(tupnames),cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("j>=k, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
					
					cond = Condition.makeCondition(cond2);
					if (k<j){
						assertTrue("k<j, supposed to be true",cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("j<=k, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
					
					cond = Condition.makeCondition(cond3);
					if (l<k){
						assertTrue("l<k, supposed to be true "+Utilities.printArray(tupnames),cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("k<=l, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
					
					cond = Condition.makeCondition(cond4);
					if (k<l){
						assertTrue("k<l, supposed to be true",cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("l<=k, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
					
					cond = Condition.makeCondition(cond5);
					if (j<l){
						assertTrue("j<l, supposed to be true "+Utilities.printArray(tupnames),cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("j>=l, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
					
					cond = Condition.makeCondition(cond6);
					if (l<j){
						assertTrue("l<j, supposed to be true",cond.compare(tupnames, tupvals, tuptypes));
					} else {
						assertFalse("l>=j, supposed to be false",cond.compare(tupnames, tupvals, tuptypes));
					}
				}
				}
			
		}
		
		
		
		
		
	}
	
	
	
}
