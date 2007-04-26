package junit;

import static org.junit.Assert.fail;

import java.nio.MappedByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dbase.StorageManager;

public class StorageManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIndexScan() {
		fail("Not yet implemented");
	}

	@Test
	public void testMakeFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testRead() {
		StorageManager storage = new StorageManager();
		MappedByteBuffer buffer;
		
		buffer = storage.read(0, 0);
		
		for (int i = 0; i < buffer.limit(); i++) {
			byte byteValue = buffer.get(i);
			char character = (char) byteValue;
			System.out.print(character);
		}
		
		BufferManager buffer = new BufferManager();
	}

	@Test
	public void testTableScan() {
		fail("Not yet implemented");
	}

	@Test
	public void testWrite() {
		fail("Not yet implemented");
	}

}
