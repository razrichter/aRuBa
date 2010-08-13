package org.jcvi.annotation.dao.factory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jcvi.annotation.dao.SmallGenomeAnnotationDAO;
import org.jcvi.annotation.dao.SmallGenomeFeatureDAO;
import org.jcvi.annotation.dao.SmallGenomeHmmHitDAO;
import org.jcvi.annotation.dao.SmallGenomePropertyDAO;
import org.jcvi.annotation.dao.SmallGenomeTaxonomyDAO;


public class SmallGenomeDAOFactory extends DAOFactory {

	private Connection conn = null;
	private String driver = "com.sybase.jdbc3.jdbc.SybDriver";
	private String url = "jdbc:sybase:Tds:SYBPROD";
	private String port = "2025";
	private String dbname;
	private String user = "access";
	private String password = "access";
	
	// The constructor and getFeatureDAO methods are called by the DAOFactory
	
	// Constructor
	public SmallGenomeDAOFactory() {
	}
	
	public SmallGenomeDAOFactory(String dbname) {
		this.dbname = dbname;
	}
	
	// Generate a new FeatureDAO
	public SmallGenomeFeatureDAO getFeatureDAO() {
		return new SmallGenomeFeatureDAO(this.createConnection());
	}

	// Generate a new AnnotationDAO
	public SmallGenomeAnnotationDAO getAnnotationDAO() {
		return new SmallGenomeAnnotationDAO(this.createConnection());
	}
	public SmallGenomeAnnotationDAO getAnnotationDAO(String dbname) {
		this.dbname = dbname;
		return new SmallGenomeAnnotationDAO(this.createConnection());
	}

	// Genome Property DAO
	public SmallGenomePropertyDAO getGenomePropertyDAO() {
		return new SmallGenomePropertyDAO();
	}
	
	public SmallGenomePropertyDAO getGenomePropertyDAO(String dbname) {
		this.setDbname(dbname);
		return new SmallGenomePropertyDAO(dbname);
	}
	
	// Generate a Taxonomy DAO
	public SmallGenomeTaxonomyDAO getTaxonomyDAO() {
		return new SmallGenomeTaxonomyDAO(this.createConnection());
	}
	// Generate a HmmHit DAO
	public SmallGenomeHmmHitDAO getHmmHitDAO() {
		return new SmallGenomeHmmHitDAO(this.createConnection());
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
