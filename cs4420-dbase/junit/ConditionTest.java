package junit;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import queries.Condition;
import queries.SimpleCondition;

/**
 * 
 * @author dkitch
 *
 */
public class ConditionTest extends TestCase{
	private static final String simpleconditiontrue = "(EQ (1) (1))";
	private static final String simpleconditionfalse = "(EQ (1) (2))";
	
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEquals() {
		Condition cond = Condition.makeCondition(simpleconditiontrue);
		System.out.println();
		assertTrue("Error: makeCondition supposed to return SimpleCondition, returns "+cond.getClass().getSimpleName()+" instead", 
				cond.getClass().getSimpleName().equals("SimpleCondition"));
		assertTrue("Supposed to be true", cond.compare(new String[] {"a"}, new String[]{"1"}));
		
		cond = Condition.makeCondition(simpleconditionfalse);
		System.out.println();
		assertTrue("Error: makeCondition supposed to return SimpleCondition, returns "+cond.getClass().getSimpleName()+" instead", 
				cond.getClass().getSimpleName().equals("SimpleCondition"));
		assertFalse("1=2 supposed to return false",cond.compare(new String[] {"a"}, new String[]{"1"}));
		
		for (int j=0; j<10; j++){
			for (char c='A'; c<='C'; c++){
				String cond1 = "(EQ ("+c+") ("+j+"))";
				String cond2 = "(EQ ("+j+") ("+c+"))";
				for (int k=0; k<10; k++){
					String[] tupnames = new String[]{"A","B","C"};
					String[] tupvals;
					if (c=='A'){
						tupvals = new String[]{""+k,"88","99"};
					} else if (c=='B'){
						tupvals = new String[]{"77", ""+k,"99"};
					} else {
						tupvals = new String[]{"77", "88", ""+k};
					}
					cond = Condition.makeCondition(cond1);
					assertTrue(((SimpleCondition)cond).getLeftHand().replace("(", "").replace(")", ""),((SimpleCondition)cond).getLeftHand().replace("(", "").replace(")", "").equals(""+c));
					if (j==k){
						assertTrue("j=k, supposed to be true "+printArray(tupnames),cond.compare(tupnames, tupvals));
					} else {
						assertFalse("j!=k, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond2);
					if (j==k){
						assertTrue("j=k, supposed to be true",cond.compare(tupnames, tupvals));
					} else {
						assertFalse("j!=k, supposed to be false",cond.compare(tupnames, tupvals));
					}
				}
			}
		}
		
		for (int j=0; j<10; j++){
			
				String cond1 = "(EQ (A) (B))";
				String cond2 = "(EQ (B) (A))";
				String cond3 = "(EQ (C) (B))";
				String cond4 = "(EQ (B) (C))";
				String cond5 = "(EQ (A) (C))";
				String cond6 = "(EQ (C) (A))";
				
				for (int k=0; k<10; k++){
					for (int l=0; l<10; l++){
					String[] tupnames = new String[]{"A","B","C"};
					String[] tupvals = new String[]{""+j,""+k,""+l};
					cond = Condition.makeCondition(cond1);
					if (j==k){
						assertTrue("j=k, supposed to be true "+printArray(tupnames),cond.compare(tupnames, tupvals));
					} else {
						assertFalse("j!=k, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond2);
					if (j==k){
						assertTrue("j=k, supposed to be true",cond.compare(tupnames, tupvals));
					} else {
						assertFalse("j!=k, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond3);
					if (l==k){
						assertTrue("l=k, supposed to be true "+printArray(tupnames),cond.compare(tupnames, tupvals));
					} else {
						assertFalse("l!=k, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond4);
					if (l==k){
						assertTrue("l=k, supposed to be true",cond.compare(tupnames, tupvals));
					} else {
						assertFalse("l!=k, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond5);
					if (j==l){
						assertTrue("j=l, supposed to be true "+printArray(tupnames),cond.compare(tupnames, tupvals));
					} else {
						assertFalse("j!=l, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond6);
					if (j==l){
						assertTrue("j=l, supposed to be true",cond.compare(tupnames, tupvals));
					} else {
						assertFalse("j!=l, supposed to be false",cond.compare(tupnames, tupvals));
					}
				}
				}
			
		}
		
		
		
		
		
	}
	
	@Test
	public void testLessThan() {
		Condition cond = Condition.makeCondition(simpleconditiontrue);
		
		for (int j=0; j<10; j++){
			
				String cond1 = "(LT (A) (B))";
				String cond2 = "(LT (B) (A))";
				String cond3 = "(LT (C) (B))";
				String cond4 = "(LT (B) (C))";
				String cond5 = "(LT (A) (C))";
				String cond6 = "(LT (C) (A))";
				
				for (int k=0; k<10; k++){
					for (int l=0; l<10; l++){
					String[] tupnames = new String[]{"A","B","C"};
					String[] tupvals = new String[]{""+j,""+k,""+l};
					cond = Condition.makeCondition(cond1);
					if (j<k){
						assertTrue("j<k, supposed to be true "+printArray(tupnames),cond.compare(tupnames, tupvals));
					} else {
						assertFalse("j>=k, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond2);
					if (k<j){
						assertTrue("k<j, supposed to be true",cond.compare(tupnames, tupvals));
					} else {
						assertFalse("j<=k, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond3);
					if (l<k){
						assertTrue("l<k, supposed to be true "+printArray(tupnames),cond.compare(tupnames, tupvals));
					} else {
						assertFalse("k<=l, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond4);
					if (k<l){
						assertTrue("k<l, supposed to be true",cond.compare(tupnames, tupvals));
					} else {
						assertFalse("l<=k, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond5);
					if (j<l){
						assertTrue("j<l, supposed to be true "+printArray(tupnames),cond.compare(tupnames, tupvals));
					} else {
						assertFalse("j>=l, supposed to be false",cond.compare(tupnames, tupvals));
					}
					
					cond = Condition.makeCondition(cond6);
					if (l<j){
						assertTrue("l<j, supposed to be true",cond.compare(tupnames, tupvals));
					} else {
						assertFalse("l>=j, supposed to be false",cond.compare(tupnames, tupvals));
					}
				}
				}
			
		}
		
		
		
		
		
	}
	
	public String printArray(String[] vals){
		String ret = "{";
		for (int j=0; j<vals.length; j++){
			ret+=vals[j]+", ";
		}
		ret = ret.substring(0, ret.length()-2)+"}";
		return ret;
	}
}
