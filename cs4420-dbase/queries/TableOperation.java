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

	/**This method returns the value of tableName.
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return true;
	}

	/**This method will set the value of tableName.
	 * @param newTableName The new value of tableName.
	 */
	public void setTableName(final String newTableName) {
		this.tableName = newTableName;
	}

}
