/**
 * 
 */
package dbase;

import java.util.ArrayList;

/**
 * @author gtg471h
 *
 */
public class Attribute {
	
	/**This represents the type of the attribute, e.g. INT, CHAR, LONG, etc.
	 * @author gtg471h
	 */
	public enum Type {
		Int, Long, Boolean, Char, Float, Double, Undeclared
	}
	
	/**The Type of this attribute, e.g. INT, LONG, CHAR, etc.*/
	private Type type;
	
	private ArrayList values;
	
	/**The number of distinct values that this attribute contains.*/
	private int distinct;
	
	/**Whether or not the value of this relation can be set to "NULL".*/
	private boolean nullable;
	
	/**The ID of the parent relation.*/
	private long parent;
	
	/**The ID of this attribute in the parent relation.*/
	private long ID;
	
	/**The name of this attribute.*/
	private String name;
	
	/**The size of this attribute in bytes.*/
	private int size;
	
	/**Returns the name of this attribute.
	 * @return The name of this attribute.
	 */
	public String getName() {
		return name;
	}

	/**Sets the name of this attribute.
	 * @param newName The new name for this attribute.
	 */
	public void setName(final String newName) {
		this.name = newName;
	}

	public Attribute() {
		
	}
	
	/**
	 * Constructor, fool!
	 * @param name what it's called
	 * @param type the data it has
	 * @param ID it's internal ID
	 */
	
	public Attribute(String name, Type type, int ID){
		this.name = name;
		this.type = type;
		this.ID = ID;
	}

	/**Tells whether or not this attribute can be set to the value of "NULL" in
	 * a record.
	 * @return Whether or not this attribute is nullable.
	 */
	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public long getParent() {
		return parent;
	}

	public void setParent(long parent) {
		this.parent = parent;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public ArrayList getValues() {
		return values;
	}

	public void setValues(ArrayList values) {
		this.values = values;
	}

	public int getDistinct() {
		return distinct;
	}

	public long getID() {
		return ID;
	}
	
	public int getSize() {
		return size;
	}
	
	public String toString(){
		return "Attribute with name "+name+" and type "+type;
	}

}
