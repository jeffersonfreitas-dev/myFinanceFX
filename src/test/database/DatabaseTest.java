package test.database;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;

import org.junit.Test;

public class DatabaseTest {
	
	Connection conn = database.DatabaseTest.getConnection();
	
	@Test
	public void testConnection () {
		assertNotNull(conn);
	}

}
