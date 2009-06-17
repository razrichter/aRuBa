package org.jcvi.annotation.dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
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
	public ArrayList<Feature> getFeatures() {
		return getFeatures(codingFeatureTypes, isCurrent);
	}
	
	public ArrayList<Feature> getFeatures(ArrayList<String> featureTypes, int isCurrent) {
		
		String sql = "SELECT a.feat_name, a.asmbl_id, a.feat_type, a.end5, a.end3, a.sequence, a.protein " +
			"FROM asm_feature AS a JOIN stan AS s ON s.asmbl_id = a.asmbl_id " +
			"AND s.iscurrent=" + isCurrent;
			
		Iterator<String> iter = featureTypes.iterator();
		if (featureTypes.size() > 0) {
			sql += "WHERE ";
			while (iter.hasNext()) {
				String type = iter.next().toString();
				String operator = (type.substring(0, 1).equals("%")) ? "LIKE" : "=";
				sql += "a.feat_type " + operator + " '" + type + "'";
				if (iter.hasNext()) { 
					sql += " OR ";
				}
			}			
		}
		
		// Get our ArrayList of feature objects
		ArrayList<Feature> features = new ArrayList<Feature>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
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

				Feature f = new Feature(featureId, type, start, end, strand);
				features.add(f);
			}
			
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return features;
	}


}
