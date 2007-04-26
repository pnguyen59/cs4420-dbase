/**
 * 
 */
package queries;

import java.util.ArrayList;

import dbase.Attribute;

/**
 * @author gtg471h
 *
 */
public class QueryOptimizer {
	
	/**
	 * Optimizes the current tree when handed the head of the tree. 
	 * If the node given isn't the head it finds the head and starts from that point.
	 * @param head The head of the Query tree.
	 */
	
	public static void OptimizeTree(Operation head)
	{		
		Operation parent, child1, child2;
		if (head.getType().equals(QueryParser.PROJECT))
		{
			OptimizeTree((Project)head.getTableOne());
			//TODO implement projection push here.
		} 
		else if (head.getType().equals(QueryParser.SELECT))
		{
			child1 = head.getTableOne();
			child1.setParent((Operation) head.getParent());
			

			head.setParent(child1);
			child1.getTableOne().setParent(head);
			if (child1.getType().equals(QueryParser.JOIN) || child1.getType().equals(QueryParser.JOIN))
			{
				ArrayList<Operation> left = new ArrayList <Operation> (), right = new ArrayList <Operation> ();
				left.add(child1.getTableOne());
				right.add(child1.getTableTwo());
				Select leftsel, rightsel;
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
				
				ArrayList <String> alist = head.getAttributes();
				for (int i = 0; i < alist.size(); i++)
				{
					
				}
			}
		}
		else if (head.getType().equals(QueryParser.CROSSJOIN))
		{
			//TODO choose order of joins
		}
		else if (head.getType().equals(QueryParser.JOIN))
		{
			//TODO choose order of joins
		}
		else if (head.getType().equals(QueryParser.TABLEOPERATION))
		{
			return;
		}
	}

}
