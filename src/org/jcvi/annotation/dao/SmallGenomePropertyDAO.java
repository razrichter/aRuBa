package org.jcvi.annotation.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.annotation.facts.Annotation;

public class SmallGenomePropertyDAO implements PropertyDAO {

	private Connection conn;
	private String dbname;
	
	// Constructors
	public SmallGenomePropertyDAO(Connection conn) {
		super();
		this.conn = conn;
	}
	public SmallGenomePropertyDAO(Connection conn, String dbname) {
		this(conn);
		this.dbname = dbname;
	}

	// Getters & Setters
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public Map<String, Object> getProperty(String propId) throws DaoException {
		String sql = "SELECT d.prop_acc, d.property, p.value " +
		"FROM prop_def d, property p where p.prop_def_id=d.prop_def_id " +
		" AND d.prop_acc='" + propId + "' " +
		" AND p.db='" + this.dbname + "'";
	
		return this.getPropertyBySQL(sql);
	}

	public Map<String, Object> getPropertyBySQL(String sql) throws DaoException {
		
		HashMap<String, Object> property = new HashMap<String, Object>();
		// System.out.println(sql);
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				property.put("id", rs.getString(1));
				property.put("definition", rs.getString(2));
				property.put("value", rs.getDouble(3));
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


	public List<Map<String, Object>> getProperties() {

		String sql = "SELECT d.prop_acc, d.property, p.value " +
		"FROM prop_def d, property p where p.prop_def_id=d.prop_def_id " +
		"AND p.db='" + this.dbname + "'";
		
		// Any Java collection is iterable, so we implement the Iterator as a List
		List<Map<String, Object>> properties = new ArrayList<Map<String, Object>>();

		try {
			Statement stmt = conn.createStatement();
			final ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				HashMap<String, Object> property = new HashMap<String, Object>();
				property.put("id", rs.getString(1));
				property.put("definition", rs.getString(2));
				property.put("value", rs.getDouble(3));
				properties.add(property);
			}

		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return properties;	
	}

	@Override
	public Iterator<Map<String, Object>> iterator() {
		final List<Map<String, Object>> properties =  getProperties();
		
		return new Iterator<Map<String, Object>>() {	
			private Map<String, Object> property = null;
			private int idx = 0;
			
			// Implement the inherited abstract methods for an Iterator
			public boolean hasNext() {
				if (property == null) property = next();
				return (property == null) ? false : true;
			}
			public Map<String, Object> next() {
				if (property != null) {
					Map<String, Object> propertyTmp = property;
					property = null;
					return propertyTmp;
				}
				return (properties.size() > idx) ? 
							properties.get(idx++) : null;
			}
			
			public void remove() {
				throw new UnsupportedOperationException("no remove allowed from Genome Property Iterator");
			}			
		};
		
	}
}
