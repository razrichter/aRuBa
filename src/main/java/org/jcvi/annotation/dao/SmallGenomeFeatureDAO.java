package org.jcvi.annotation.dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.annotation.facts.Feature;

public class SmallGenomeFeatureDAO implements FeatureDAO {

	private Connection conn;
	private ArrayList<String> codingFeatureTypes = new ArrayList<String>();
	private int isCurrent = 1;
	
	public SmallGenomeFeatureDAO() {
		// Our default types of coding features
		codingFeatureTypes.add("%RNA");
		codingFeatureTypes.add("ORF");
	}
	public SmallGenomeFeatureDAO(Connection conn) {
		this();
		this.conn = conn;
	}

	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public ArrayList<String> getCodingFeatureTypes() {
		return codingFeatureTypes;
	}

	public void setCodingFeatureTypes(ArrayList<String> codingFeatureTypes) {
		this.codingFeatureTypes = codingFeatureTypes;
	}

	public int getIsCurrent() {
		return isCurrent;
	}

	public void setIsCurrent(int isCurrent) {
		this.isCurrent = isCurrent;
	}

	@Override
	public Feature getFeature(String code) {
		String sql = "SELECT feat_type, end5, end3, sequence, protein " +
					"FROM asm_feature WHERE feat_name='" + code + "'";
		Connection conn = this.getConn(); 
		
		Feature feature = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				String type = rs.getString(1);
				int start = rs.getInt(2);
				int end = rs.getInt(3);
				int strand = 1;
				if (end < start) {
					strand = -1;
					int tmp = end;
					end = start;
					start = tmp;
				}
				feature = new Feature(code, type, start, end, strand);
				rs.close();
				stmt.close();
			}
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		
		return feature;
	}
	
	@Override
	public Iterator<Feature> getFeatures() {
		return getFeatures(codingFeatureTypes, isCurrent);
	}
	
	public Iterator<Feature> getFeatures(ArrayList<String> featureTypes, int isCurrent) {
		
		String sql = "SELECT a.feat_name, a.asmbl_id, a.feat_type, a.end5, a.end3, a.sequence, a.protein " +
			"FROM asm_feature AS a JOIN stan AS s ON s.asmbl_id = a.asmbl_id " +
			"AND s.iscurrent=" + isCurrent;
			
		Iterator<String> types = featureTypes.iterator();
		if (featureTypes.size() > 0) {
			sql += "WHERE ";
			while (types.hasNext()) {
				String type = types.next().toString();
				String operator = (type.substring(0, 1).equals("%")) ? "LIKE" : "=";
				sql += "a.feat_type " + operator + " '" + type + "'";
				if (types.hasNext()) { 
					sql += " OR ";
				}
			}			
		}

		try {
			Statement stmt = conn.createStatement();
			final ResultSet rs = stmt.executeQuery(sql);
			return this.getFeatureIteratorByResultSet(rs);
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return null;	
	}

	public Iterator<Feature> getFeatureIteratorByResultSet(final ResultSet rs) {
		
		// Use an anonymous inner class to return an Iterator of Feature objects
		return new Iterator<Feature>() {	
			private Feature feature = null;
			
			public boolean hasNext() {
				// Get/Set the next feature object
				if (feature == null) feature = next();
				return (feature == null) ? false : true;
			}
			public Feature next() {
				
				if (feature != null) {
					Feature featureTmp = feature;
					feature = null;
					return featureTmp;
				}
				
				try {
					if (rs.next()) {
						String featureId = rs.getString(1);
						String type = rs.getString(3);
						int start = rs.getInt(4);
						int end = rs.getInt(5);
						int strand = 1;
						if (end < start) {
							strand = -1;
							int tmp = end;
							end = start;
							start = tmp;
						}
						return new Feature(featureId, type, start, end, strand);
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}
			public void remove() {
				throw new UnsupportedOperationException("no remove allowed from Feature Iterator");
			}
		};
	}
	
		
}
