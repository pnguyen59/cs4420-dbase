/**
 * 
 */
package queries;

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
				//TODO split the select here and recursively send it down the line
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
