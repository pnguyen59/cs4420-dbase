/**
 * 
 */
package dbase;

import java.nio.ByteBuffer;
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
		Int, Long, Boolean, Char, Float, Double, DateTime, Undeclared
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
		this.length = length;
	}

	/**Creates a new instance of the type Attribute.  This particular 
	 * constructor takes in a variable for the length of the attribute,
	 * making this construct for use with type CHAR only.
	 * @param newName The name of the new attribute.
	 * @param newAttributeID The ID of the new attribute.
	 * @param newRelationID The ID of the relation holding the attribute.
	 * @param newType The Attribute.Type type of the new attribute.
	 * @param newNullable Whether or not this attribute can be null.
	 * @param newDistinct The number of distinct values this attribute has.
	 * @param newLength The length of this attribute.
	 */
	public Attribute(final String newName, final long newAttributeID,
			final long newRelationID, final Attribute.Type newType, 
			final char newNullable, final int newDistinct, 
			final int newLength) {
		
		//Assign the things to the new attribute from the constructor
		this.name = newName;
		this.ID = newAttributeID;
		this.parent = newRelationID;
		this.type = newType;
		this.nullable = (newNullable == 't');
		this.distinct = newDistinct;
		this.length = newLength;
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
		} else if (type == Attribute.Type.DateTime){
			return LONG_SIZE;
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
	
	/**This method converts the string representation of a type to the 
	 * <b>Type</b> representation of the type.
	 * @param newType The string representation of the type we want.
	 * @return The <b>Type</b> representation of the passed string.
	 */
	public static Type stringToType(final String newType) {
		
		Type returnType = Type.Undeclared;
		
		if (newType.toLowerCase().equalsIgnoreCase("int")) {
			returnType = Attribute.Type.Int;
		} else if (newType.toLowerCase().equalsIgnoreCase("long")) {
			returnType = Attribute.Type.Long;
		} else if (newType.toLowerCase().equalsIgnoreCase("boolean") 
				|| newType.toLowerCase().equalsIgnoreCase("bool")) {
			returnType = Attribute.Type.Boolean;
		} else if (newType.toLowerCase().equalsIgnoreCase("char") 
				|| newType.toLowerCase().equalsIgnoreCase("character")) {
			returnType = Attribute.Type.Char;
		} else if (newType.toLowerCase().equalsIgnoreCase("float")) {
			returnType = Attribute.Type.Float;
		} else if (newType.toLowerCase().equalsIgnoreCase("double")) {
			returnType = Attribute.Type.Double;
		} else if (newType.toLowerCase().equalsIgnoreCase("datetime")) {
			returnType = Attribute.Type.DateTime;
		} else {
			returnType = Attribute.Type.Undeclared;
		}	
		return returnType;
	}
	
	public ByteBuffer writeCrapToBuffer(){
		ByteBuffer buf = ByteBuffer.wrap(new byte[SystemCatalog.ATT_REC_SIZE]);
		for (int j=0; j<15; j++){
			if (j<name.length()){
				char ch = name.charAt(j);
				buf.putChar(j*2,ch);
			} else {
				buf.putChar(j*2,BufferManager.NULL_CHARACTER);
			}
		}
		buf.putLong(30,ID);
		
		if (nullable)buf.putChar(38,'t');
		else buf.putChar(38, 'f');
		
		buf.putChar(40,type.toString().charAt(0));
		
		buf.putLong(42,parent);
		
		buf.putInt(50,distinct);
		
		buf.putInt(54,length);
		
		return buf;
	}
	
	public String toString(){
		String ret = "Attribute with name "+name+" and type "+type;
		if (this.type == Type.Char) ret+=" of length "+length;
		return ret;
	}

}
