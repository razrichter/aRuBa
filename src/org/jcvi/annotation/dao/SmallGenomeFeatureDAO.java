package org.jcvi.annotation.dao;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;

public class SmallGenomeFeatureDAO implements FeatureDAO {

	private Connection conn;
	private List<String> codingFeatureTypes = new ArrayList<String>();
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

	@Override
	public Iterator<Feature> iterator() {
		return iterator(codingFeatureTypes, isCurrent);
	}

	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public List<String> getCodingFeatureTypes() {
		return codingFeatureTypes;
	}

	public void setCodingFeatureTypes(List<String> codingFeatureTypes) {
		this.codingFeatureTypes = codingFeatureTypes;
	}

	public int getIsCurrent() {
		return isCurrent;
	}

	public void setIsCurrent(int isCurrent) {
		this.isCurrent = isCurrent;
	}

	@Override
	public Feature getFeature(String featureId) {
		String sql = "SELECT feat_name, feat_type, end5, end3 " +
					"FROM asm_feature WHERE feat_name='" + featureId + "'";
		
		return getFeatureBySQL(sql);
	}
	
	public Feature getFeatureById(int feat_id) {
		String sql = "SELECT feat_name, feat_type, end5, end3 " +
					"FROM asm_feature WHERE feat_id=" + feat_id;
		
		return getFeatureBySQL(sql);
	}

	public Feature getFeatureBySQL(String sql) {

		Connection conn = this.getConn(); 
		
		Feature feature = null;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// TODO: Set the genome, and assign genome properties to genome 
			
			if (rs.next()) {

				String featureId = rs.getString(1);
				String name = rs.getString(1);
				String type = rs.getString(2);
				int start = rs.getInt(3);
				int end = rs.getInt(4);
				int strand = 1;
				if (end < start) {
					strand = -1;
					int tmp = end;
					end = start;
					start = tmp;
				}
				feature = new Feature(featureId, type, start, end, strand, name);
			}
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		} finally
		{
			this.close(stmt);
		}
		
		return feature;
	}
	
	public Iterator<Feature> iterator(List<String> featureTypes, int isCurrent) {
		
		String sql = "SELECT a.feat_id, a.feat_name, a.asmbl_id, a.feat_type, a.end5, a.end3, a.sequence, a.protein " +
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

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			final ResultSet rs = stmt.executeQuery(sql);
			return this.getFeatureIterator(rs);
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return null;	
	}

	public Iterator<Feature> getFeatureIterator(final ResultSet rs) {
		
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
						String featureId = rs.getString(2);
						String name = rs.getString(2);
						String type = rs.getString(4);
						int start = rs.getInt(5);
						int end = rs.getInt(6);
						int strand = 1;
						if (end < start) {
							strand = -1;
							int tmp = end;
							end = start;
							start = tmp;
						}
						return new Feature(featureId, type, start, end, strand, name);
						
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
	
	// Annotations
	public Iterator<Annotation> getAnnotations(Feature feature) {
		
		if (feature == null) {
			throw new IllegalArgumentException("A valid feature object is required");
		}

		String sql = "SELECT i.com_name, i.gene_sym, i.ec# FROM asm_feature a, ident i, stan s" +
				" WHERE a.feat_name = i.feat_name AND s.asmbl_id = a.asmbl_id" +
				" AND s.iscurrent=" + this.isCurrent +
				" AND a.feat_id=" + feature.getFeatureId();
		
		Connection conn = this.getConn(); 
		
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return this.getAnnotationIterator(rs);

		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return null;
	}
	
	public Iterator<Annotation> getAnnotations() {

		String sql = "SELECT i.com_name, i.gene_sym, i.ec# FROM asm_feature a, ident i, stan s" +
		" WHERE a.feat_name = i.feat_name AND s.asmbl_id = a.asmbl_id" +
		" AND s.iscurrent=" + this.isCurrent;

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			final ResultSet rs = stmt.executeQuery(sql);
			return this.getAnnotationIterator(rs);
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return null;	
	}

	public Iterator<Annotation> getAnnotationIterator(final ResultSet rs) {
		
		// Use an anonymous inner class to return an Iterator of Feature objects
		return new Iterator<Annotation>() {	
			private Annotation annot = null;
			
			public boolean hasNext() {
				// Get/Set the next annotation object
				if (annot == null) annot = next();
				return (annot == null) ? false : true;
			}
			public Annotation next() {
				
				if (annot != null) {
					Annotation annotationTmp = annot;
					annot = null;
					return annotationTmp;
				}
				
				try {
					if (rs.next()) {
						Annotation nextAnnot = new Annotation(conn.toString());
						nextAnnot.setCommonName(rs.getString(1));
						nextAnnot.setGeneSymbol(rs.getString(2));
						nextAnnot.setEcNumbers(rs.getString(3));
						
						
						return nextAnnot;
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
