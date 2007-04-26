package junit;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dbase.Attribute;
import dbase.BufferManager;
import dbase.Relation;
import dbase.StorageManager;

public class RelationTest extends TestCase{

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddRecord() {
		
		final int characterSize = 15;
		
		//Create a relation with a set of attributes
		Relation relation = new Relation("RELATION", 0);
		relation.addAttribute("INTEGER_1", Attribute.Type.Int, 0);
		relation.addAttribute("CHARACTER_1", Attribute.Type.Char, 1, 
			characterSize);
		relation.addAttribute("INTEGER_2", Attribute.Type.Int, 2);
		
		//Create a block and add things to it for the relation to parse        
		byte [] byteArray = new byte [4096];
		ByteBuffer block = ByteBuffer.wrap(byteArray);
	
		//Now try adding attributes in one order
		String attributesOne = "(INTEGER_1, CHARACTER_1, INTEGER_2)";
		relation.addRecord(block, "(1000, RECORD_1, 1001)", attributesOne);
		relation.addRecord(block, "(2000, RECORD_2, 2002)", attributesOne);
		String [][] records = relation.parseBlock(block);
		//See what we got back
		assertTrue("Should have been 1000, was " 
			+ Integer.parseInt(records[0][0]),
			Integer.parseInt(records[0][0]) == 1000);
		assertTrue("Should have been \"RECORD_!\", was " 
				+ records[0][1],
				records[0][1].equalsIgnoreCase("RECORD_1"));
		assertTrue("Should have been 1001, was " 
				+ Integer.parseInt(records[0][2]),
				Integer.parseInt(records[0][2]) == 1001);
		
		//Now try adding attributes in a different order
//		Create a relation with a set of attributes
		relation = new Relation("RELATION", 0);
		relation.addAttribute("INTEGER_1", Attribute.Type.Int, 0);
		relation.addAttribute("CHARACTER_1", Attribute.Type.Char, 1, 
			characterSize);
		relation.addAttribute("INTEGER_2", Attribute.Type.Int, 2);
		byteArray = new byte [4096];
		block = ByteBuffer.wrap(byteArray);
		String attributesTwo = "(INTEGER_2, INTEGER_1, CHARACTER_1)";
		relation.addRecord(block, "(1001, 1000, RECORD_1)", attributesTwo);
		relation.addRecord(block, "(2002, 2000, RECORD_2)", attributesTwo);
		//System.out.println(block.asCharBuffer());
		records = relation.parseBlock(block);
		//System.out.println(records.length);
		//See what we got back
		assertTrue("Should have been 1000, was " 
			+ Integer.parseInt(records[0][0]),
			Integer.parseInt(records[0][0]) == 1000);
		assertTrue("Should have been \"RECORD_!\", was " 
				+ records[0][1],
				records[0][1].equalsIgnoreCase("RECORD_1"));
		assertTrue("Should have been 1001, was " 
				+ Integer.parseInt(records[0][2]),
				Integer.parseInt(records[0][2]) == 1001);	
		
	}
	
	@Test
	public void testAddAttribute() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddIndex() {
		fail("Not yet implemented");
	}

	@Test
	public void testContainsIndex() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsert() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsLastBlockFull() {
		fail("Not yet implemented");
	}

	@Test
	public void testOpen() {
	
		
	}

	@Test
	public void testParseBlock() {
		
		final int characterSize = 15;
		
		//Create a relation with a set of attributes
		Relation relation = new Relation("RELATION", 0);
		relation.addAttribute("INTEGER_1", Attribute.Type.Int, 0);
		relation.addAttribute("CHARACTER_1", Attribute.Type.Char, 1, 
			characterSize);
		relation.addAttribute("INTEGER_2", Attribute.Type.Int, 2);
		
		//Create a block and add things to it for the relation to parse        
		byte [] byteArray = new byte [4096];
		ByteBuffer block = ByteBuffer.wrap(byteArray);
		/*//Write the record 1000 "RECORD_1" 1001
		block.putInt(1000);
		relation.writeString(block, "RECORD_1", 4, characterSize);
		block.putInt(1001);
		//Write the record 2000 "RECORD_2" 2002
		block.putInt(2000);
		relation.writeString(block, "RECORD_2", 42, characterSize);
		block.putInt(2002);*/
		String attributesOne = "(INTEGER_1, CHARACTER_1, INTEGER_2)";
		relation.addRecord(block, "(1000, RECORD_1, 1001)", attributesOne);
		relation.addRecord(block, "(2000, RECORD_2, 2002)", attributesOne);
		
		//Now try to get all that stuff out of the block
		String [][] records = relation.parseBlock(block);

		//See what we got back
		assertTrue("Should have been 1000, was " 
			+ Integer.parseInt(records[0][0]),
			Integer.parseInt(records[0][0]) == 1000);
		assertTrue("Should have been \"RECORD_!\", was " 
				+ records[0][1],
				records[0][1].equalsIgnoreCase("RECORD_1"));
		assertTrue("Should have been 1001, was " 
				+ Integer.parseInt(records[0][2]),
				Integer.parseInt(records[0][2]) == 1001);
		//See that the records come out right
		//assertTrue(records[0][0].equalsIgnoreCase("Cow"));		
	}
	
	@Test
	public void testParseRecord() {
		//Create a relation with a set of attributes
		Relation relation = new Relation("Chicken", 0);
		relation.addAttribute("Monkey", Attribute.Type.Int, 0);
		relation.addAttribute("Walrus", Attribute.Type.Int, 1);
		relation.addAttribute("Chicken", Attribute.Type.Char, 2, 10);
		
		//Now see if it can parse a record
		//Create a ByteBuffer and pack random stuff onto it
		byte [] bytes = new byte [StorageManager.BLOCK_SIZE];
		ByteBuffer block = ByteBuffer.wrap(bytes);
		
		block.putInt(11);
		block.putInt(2);
		
		//Loop through and add a String
		String word = "Walrus";
		for (int i = 0; i < word.length(); i++) {
			block.putChar(word.charAt(i));
		}
		
		//Now have the relation parse it out and see what happens
		String [] parsed = relation.parseRecord(block);
		
		//Print the result
		for (int i = 0; i < parsed.length; i++) {
			System.out.print(parsed[i]);
			System.out.print("#");
		}
		System.out.println("\n");
		
		//Now try it in another order with the CHAR in the middle
		relation = new Relation("Chicken", 0);
		relation.addAttribute("Monkey", Attribute.Type.Int, 0);
		relation.addAttribute("Walrus", Attribute.Type.Char, 1, 10);
		relation.addAttribute("Chicken", Attribute.Type.Int, 2);
		
		//Create a ByteBuffer and pack random stuff onto it
		bytes = new byte [StorageManager.BLOCK_SIZE];
		block = ByteBuffer.wrap(bytes);
		block.putInt(11);
		relation.writeString(block, word, 4, 20);
		block.putInt(24, 2);
		
		//Now have the relation parse it out and see what happens
		parsed = relation.parseRecord(block);
		
		//Print the result
		for (int i = 0; i < parsed.length; i++) {
			System.out.print(parsed[i]);
			System.out.print("#");
		}
		System.out.println("\n");
	}

	@Test
	public void testParseString() {
		//Create a ByteBuffer and pack random stuff onto it
		byte [] bytes = new byte [StorageManager.BLOCK_SIZE];
		ByteBuffer block = ByteBuffer.wrap(bytes);
		
		block.putInt(11);
		block.putFloat((float) 3.007);
		
		//Loop through and add a String
		String word = "Walrus";
		for (int i = 0; i < word.length(); i++) {
			block.putChar(word.charAt(i));
		}
		
		//Now we get them back out
		String wordOut = Relation.parseString(block, 8, 12);
		System.out.println(wordOut);

	}

	@Test
	public void testIndexOfAttribute() {
		fail("Not yet implemented");
	}

}
