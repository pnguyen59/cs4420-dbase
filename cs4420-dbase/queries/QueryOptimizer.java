/**
 * 
 */
package queries;

import java.util.ArrayList;

/**
 * @author gtg471h
 * 
 */
public class QueryOptimizer {

	/**
	 * Optimizes the current tree when handed the head of the tree. If the node
	 * given isn't the head it finds the head and starts from that point.
	 * 
	 * @param head
	 *            The head of the Query tree.
	 */

	public static void OptimizeTree(Operation head)
	{		
		Operation parent, child1, child2;
		if (head.getType().equals(QueryParser.PROJECT))
		{
			OptimizeTree((Project)head.getTableOne());
			// TODO implement projection push here.
		} 
		else if (head.getType().equals(QueryParser.SELECT))
		{
			child1 = head.getTableOne();
			
			if (child1.getType().equals(QueryParser.JOIN) || child1.getType().equals(QueryParser.JOIN))
			{
				ArrayList<Operation> left = new ArrayList <Operation> (), right = new ArrayList <Operation> ();
				left.add(child1.getTableOne());
				right.add(child1.getTableTwo());
				Select leftsel, rightsel;
				leftsel = (Select) Operation.makeOperation("Select");
				boolean finishedleft = true, finishedright = true;
				while (finishedleft)
				{
					for (int j = 0; j < left.size(); j++)
					{
						Operation blah = left.get(j);
						if (blah.getType().equals(QueryParser.JOIN) || blah.getType().equals(QueryParser.CROSSJOIN))
						{
							left.add(blah.getTableTwo());
						}
						if (blah.getTableOne() != null)
						{
							blah = blah.getTableOne();
						}
					}
					finishedleft = false;
					for (int k = 0; k < left.size(); k++)
					{
						finishedleft = finishedleft || (left.get(k) != null);
					}
				}
				while (finishedright)
				{
					for (int j = 0; j < right.size(); j++)
					{
						Operation blah = right.get(j);
						if (blah.getType().equals(QueryParser.JOIN) || blah.getType().equals(QueryParser.CROSSJOIN))
						{
							right.add(blah.getTableTwo());
						}
						if (blah.getTableOne() != null)
						{
							blah = blah.getTableOne();
						}
					}
					finishedright = false;
					for (int k = 0; k < left.size(); k++)
					{
						finishedright = finishedright || (right.get(k) != null);
					}
				}
				
				Condition cond = ((Select) head).getCondition();
				
				if (cond.comparison.equals(QueryParser.OR))
				{
					OptimizeTree(head.getTableOne());
					return;
				}
				boolean cont;
						
				cont = (condtraverse(((AndOrCondition) cond).getLeftHand()) && condtraverse(((AndOrCondition) cond).getRightHand()));
				
				if (!cont)
				{
					OptimizeTree(head.getTableOne());
					return;
				}
				
				ArrayList <String> alist = head.getAttributes();
				for (int i = 0; i < alist.size(); i++)
				{
					child1.setParent((Operation) head.getParent());
					
					head.setParent(child1);
					child1.getTableOne().setParent(head);
				}
			}
		}
		else if (head.getType().equals(QueryParser.CROSSJOIN))
		{
			// TODO choose order of joins
		}
		else if (head.getType().equals(QueryParser.JOIN))
		{
			// TODO choose order of joins
		}
		else if (head.getType().equals(QueryParser.TABLEOPERATION))
		{
			return;
		}
	}

	private static boolean condtraverse(Condition cond) {
		if (cond.condition.equals(QueryParser.AND))
		{
			return (condtraverse(((AndOrCondition) cond).getLeftHand()) && condtraverse(((AndOrCondition) cond).getRightHand()));
		} 
		else if (cond.condition.equals(QueryParser.OR))
		{
			return false;
		}
		return true;
	}

	public static void OptimizeProjects(Operation root, ArrayList<String> attrs) {

	}

}
