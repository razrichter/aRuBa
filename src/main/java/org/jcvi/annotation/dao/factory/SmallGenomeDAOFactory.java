package org.jcvi.annotation.dao.factory;
import org.jcvi.annotation.dao.*;
import java.sql.*;
// import com.sybase.jdbc.*;

public class SmallGenomeDAOFactory extends DAOFactory {

	private Connection conn = null;
	private String driver = "com.sybase.jdbc3.jdbc.SybDriver";
	private String url = "jdbc:sybase:Tds:SYBTIGR";
	private String port = "2025";
	private String dbname = "common";
	private String user = "access";
	private String password = "access";
	
	// The constructor and getFeatureDAO methods are called by the DAOFactory
	
	// Constructor
	public SmallGenomeDAOFactory() {
		super();
		conn = this.createConnection();
	}
	
	// Generate a new FeatureDAO
	public SmallGenomeFeatureDAO getFeatureDAO() {
		return new SmallGenomeFeatureDAO(conn);
	}
	public SmallGenomeFeatureDAO getFeatureDAO(String dbname) {
		this.dbname = dbname;
		conn = this.createConnection();
		return new SmallGenomeFeatureDAO(conn);
	}

	// Generate a new AnnotationDAO
	public SmallGenomeAnnotationDAO getAnnotationDAO() {
		return new SmallGenomeAnnotationDAO(conn);
	}
	public SmallGenomeAnnotationDAO getAnnotationDAO(String dbname) {
		this.dbname = dbname;
		conn = this.createConnection();
		return new SmallGenomeAnnotationDAO(conn);
	}
	
	// Getters and Setters
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	// Database stuff
	public boolean loadJdbcDriver() {
		
		// only necessary prior to JDBC 4.0
		try {
			Class.forName( driver ).newInstance();
			return true;
		}
		catch ( Exception e ){
			e.printStackTrace();
		}
		return false;
	}
	public Connection createConnection() {
		
		// Load the JDBC driver
		loadJdbcDriver();
		
		try {
			conn = DriverManager.getConnection(url + ":" + port + "/" + dbname, user, password);
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return conn;
	}
	
}
