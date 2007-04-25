package queries;

public class TableOperation extends Operation {

	private String tableName;
	
	public TableOperation(final String newTableName) {
		tableName = newTableName;
	}
	
	@Override
	public long calculateCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public int getChildCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return true;
	}

}
