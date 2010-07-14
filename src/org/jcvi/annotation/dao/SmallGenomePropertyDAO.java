package org.jcvi.annotation.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.Property;

public class SmallGenomePropertyDAO implements PropertyDAO {

	// Standard connection parameters (like SmallGenomeDAOFactory)
	private Connection conn = null;
	private String driver = "com.sybase.jdbc3.jdbc.SybDriver";
	private String url = "jdbc:sybase:Tds:SYBPROD"; // sybprd (Sybase server), SYBPROD (host)
	private String port = "2025";
	private String dbname = "common";
	private String user = "access";
	private String password = "access";
	
	// But SmallGenomePropertyDAO is unique because we always want to connect
	// to common database, and then we use the small genome database name
	// to filter queries
	private String sg_dbname;
	
	// Constructors
	public SmallGenomePropertyDAO() {
		super();
		this.createConnection();
	}
	public SmallGenomePropertyDAO(Connection conn) {
		this.conn = conn;
	}
	public SmallGenomePropertyDAO(String sg_dbname) {
		this(); // Connect to common database
		this.setSg_dbname(sg_dbname); // But query according to sg_dbname
	}

	public GenomeProperty getProperty(String propId) throws DaoException {
		String sql = "SELECT d.prop_acc, d.property, p.value " +
		"FROM prop_def d, property p where p.prop_def_id=d.prop_def_id " +
		" AND d.prop_acc='" + propId + "' " +
		" AND p.db='" + this.sg_dbname + "'";
	
		return this.getPropertyBySQL(sql);
	}

	public GenomeProperty getPropertyBySQL(String sql) throws DaoException {
		
		Connection conn = this.getConn(); 
		
		GenomeProperty property = null;
		// System.out.println(sql);
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				property = GenomeProperty.create(rs.getString(1));
				property.setDefinition(rs.getString(2));
				property.setValue(rs.getDouble(3));
			} else {
				throw new DaoException("Genome Property not found.");
			}
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return property;
	}


	public List<GenomeProperty> getProperties() {

		Connection conn = this.getConn(); 
		
		String sql = "SELECT d.prop_acc, d.property, p.value " +
		"FROM prop_def d, property p where p.prop_def_id=d.prop_def_id " +
		"AND p.db='" + this.sg_dbname + "'";
		
		// Any Java collection is iterable, so we implement the Iterator as a List
		List<GenomeProperty> properties = new ArrayList<GenomeProperty>();

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			final ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				GenomeProperty property = GenomeProperty.create(rs.getString(1));
				property.setDefinition(rs.getString(2));
				property.setValue(rs.getDouble(3));
				properties.add(property);
			}
			stmt.close();

		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		} finally 
		{
			this.close(stmt);
		}
		return properties;	
	}

	public Iterator<Property> iterator() {
		final Iterator<GenomeProperty> iter = getProperties().iterator();
		return new Iterator<Property>() {
			public boolean hasNext() {
				return iter.hasNext();
			}
			public GenomeProperty next() {
				return iter.next();
			}
			public void remove() {
				iter.remove();
			}			
		};
	}
	
	
	/* Database Connection */
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

	public void setSg_dbname(String sg_dbname) {
		this.sg_dbname = sg_dbname;
	}
	public String getSg_dbname() {
		return sg_dbname;
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

	public void close (Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
