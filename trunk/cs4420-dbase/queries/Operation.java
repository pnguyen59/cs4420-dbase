package queries;

import java.util.ArrayList;

public abstract class Operation {

	protected String type;
	
	protected int queryID;
	
	protected int executionOrder;
	
	protected int tableOneID;
	
	protected int tableOneAccess;
	
	protected int resultTableID;

	public void setType(final String newType) {
		this.type = newType;
	}

	public abstract long calculateCost();
}
