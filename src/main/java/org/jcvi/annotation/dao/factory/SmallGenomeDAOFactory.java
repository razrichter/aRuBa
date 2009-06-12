package org.jcvi.annotation.dao.factory;
import org.jcvi.annotation.dao.*;
import java.sql.*;

public class SmallGenomeDAOFactory extends DAOFactory {

	private static String DBURL = "jdbc:odbc:SYBASE";
	private static String USER = "access";
	private static String PSWD = "access";
	private static Connection conn = null;
	
	public static Connection createConnection() {
		
		try {
			// Load the JDBC driver
			// Class.forName(DRIVER); // only necessary prior to JDBC 4.0
			System.out.println("Attempting database connection " + DBURL);
			
			// Establish the database connection
			conn = DriverManager.getConnection(DBURL, USER, PSWD);

		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return conn;
	}
	
	public static Connection getCon() {
		return conn;
	}

	public static void setCon(Connection conn) {
		SmallGenomeDAOFactory.conn = conn;
	}

	public FeatureDAO getFeatureDAO() {
		return new SmallGenomeFeatureDAO();
	}
}
