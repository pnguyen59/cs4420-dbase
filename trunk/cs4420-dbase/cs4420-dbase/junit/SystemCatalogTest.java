package junit;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dbase.SystemCatalog;

public class SystemCatalogTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testSelectFromTable() {
		
		SystemCatalog catalog = new SystemCatalog();
		catalog.createTable("CREATE TABLE t(anint int, achar char 10, achar2 char 20)", "key");
    	catalog.insert("INSERT INTO t (achar2, achar) VALUES(a1, abcdefg)");
    	catalog.insert("INSERT INTO t (achar2, achar) VALUES(a2, abcdefg)");
    	catalog.insert("INSERT INTO t (achar2, achar) VALUES(a2, abcdefh)");
    	catalog.insert("INSERT INTO t (achar2, achar) VALUES(a3, abcdefh)");
    	catalog.insert("INSERT INTO t (achar2, achar) VALUES(a3, abcdefh)");
		String [] result = catalog.selectFromTable
			("SELECT * FROM TABLE t WHERE achar2 = a1");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index]);
		}
		System.out.println();
		result = catalog.selectFromTable
			("SELECT * FROM TABLE t WHERE achar2 = a2");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index]);
		}
		System.out.println();
		result = catalog.selectFromTable
		("SELECT * FROM TABLE t WHERE achar2 = a3");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index]);
		}
		System.out.println();
		result = catalog.selectFromTable
		("SELECT * FROM TABLE t WHERE achar = abcdefg");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index]);
		}
		System.out.println();
		result = catalog.selectFromTable
		("SELECT * FROM TABLE t WHERE achar = abcdefh");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index]);
		}
		System.out.println();
		//catalog.selectFromTable("SELECT WALRUS CHICKEN FROM TABLE POO [WHERE XXXX");
	}
	
	@Test
	public void testSelectFromCatalog() {
		SystemCatalog catalog = new SystemCatalog();
		catalog.createTable("CREATE TABLE One(anIntOne int, aCharOne char 10)", "key");
		catalog.createTable("CREATE TABLE Two(anIntTwo int, aCharTwo char 10)", "key");
		
		String [] result = catalog.selectFromCatalog(
				"SELECT * FROM ATTRIBUTE_CATALOG.ac");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index]);
		}
		System.out.println();
		result = catalog.selectFromCatalog(
		"SELECT * FROM RELATION_CATALOG.rc");
		for (int index = 0; index < result.length; index++) {
			System.out.println(result[index]);
		}
		
	}
}
