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

	/**This is the size in bytes of a single character.*/
	public static final int CHAR_SIZE = Character.SIZE / Byte.SIZE;
	
	/**This is the size in bytes of a float.*/
	public static final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;
	
	/**This is the size in bytes of a double.*/
	public static final int DOUBLE_SIZE = Double.SIZE / Byte.SIZE;
	
	/**This is the size in bytes of an integer.*/
	public static final int INT_SIZE = Integer.SIZE / Byte.SIZE;
	
	/**This is the size in bytes of a Long.*/
	public static final int LONG_SIZE = Long.SIZE / Byte.SIZE;
	
	/**The Type of this attribute, e.g. INT, LONG, CHAR, etc.*/
	private Type type;
	
	private ArrayList values;
	
	/**Index names*/
	
	private char[] index = new char[SystemCatalog.stringlength];
	
	private char[] indexd = new char[SystemCatalog.stringlength];
	
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
	
	/**The length of characters in the Attribute*/
	private int length;
	
	/**The size of this attribute in bytes.*/
	private int size;
	
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
	
	
	/**This is the constructor for new attributes of the type CHAR, which
	 * includes the length of the new attribute.
	 * @param name The name of this new CHAR.
	 * @param type The type of this new CHAR.
	 * @param ID The ID of this new CHAR.
	 * @param length  The length of this new CHAR.
	 */
	public Attribute(String name, Type type, int ID, int length){
		this.name = name;
		this.type = type;
		this.ID = ID;
	}

	public int getCharLength() {
		return length;
	}
	
	public int getDistinct() {
		return distinct;
	}

	public long getID() {
		return ID;
	}

	public char[] getIndex() {
		return index;
	}

	public char[] getIndexd() {
		return indexd;
	}

	/**Returns the name of this attribute.
	 * @return The name of this attribute.
	 */
	public String getName() {
		return name;
	}

	public long getParent() {
		return parent;
	}

	public int getSize() {
		
		//Calculate the size of this biatch
		if (type == Attribute.Type.Int) {
			return INT_SIZE;
		} else if (type == Attribute.Type.Char) {
			return CHAR_SIZE * length;
		} else if (type == Attribute.Type.Long) {
			return LONG_SIZE;
		} else if (type == Attribute.Type.Float) {
			return FLOAT_SIZE;
		} else if (type == Attribute.Type.Double) {
			return DOUBLE_SIZE;
		}
		
		return size;
	}

	public Type getType() {
		return type;
	}

	public ArrayList getValues() {
		return values;
	}

	/**Tells whether or not this attribute can be set to the value of "NULL" in
	 * a record.
	 * @return Whether or not this attribute is nullable.
	 */
	public boolean isNullable() {
		return nullable;
	}

	public void setCharLength(int length) {
		this.length = length;
	}
	
	public void setIndex(char[] index) {
		this.index = index;
	}
	
	public void setIndexd(char[] indexd) {
		this.indexd = indexd;
	}

	/**Sets the name of this attribute.
	 * @param newName The new name for this attribute.
	 */
	public void setName(final String newName) {
		this.name = newName;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public void setParent(long parent) {
		this.parent = parent;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public void setValues(ArrayList values) {
		this.values = values;
	}
	
	public String toString(){
		return "Attribute with name "+name+" and type "+type;
	}

}
