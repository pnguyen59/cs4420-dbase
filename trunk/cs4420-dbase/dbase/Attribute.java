/**
 * 
 */
package dbase;

/**
 * @author gtg471h
 *
 */
import java.util.*;

public class Attribute {
	
	public enum Type {
		Int, Long, String, Boolean, Character, Float, Double
	}
	
	private Type type;
	private ArrayList values;
	private int distinct;
	private boolean nullable;
	private long parent;
	private long ID;
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Attribute() {
		
	}

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

}
