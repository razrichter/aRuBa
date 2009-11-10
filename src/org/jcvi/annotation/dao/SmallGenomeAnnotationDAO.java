package org.jcvi.annotation.dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;

public class SmallGenomeAnnotationDAO implements AnnotationDAO {

	private Connection conn;
	private int isCurrent = 1;
	
	public SmallGenomeAnnotationDAO() {
		// Any initialization
	}
	
	public SmallGenomeAnnotationDAO(Connection conn) {
		this();
		this.conn = conn;
	}

	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public int getIsCurrent() {
		return isCurrent;
	}

	public void setIsCurrent(int isCurrent) {
		this.isCurrent = isCurrent;
	}

	@Override
	public Annotation getAnnotation(String annotId) {
		
		return null;
	}

	public Iterable<Annotation> getAnnotations(Feature feat) {
		List<Annotation> annotations = new ArrayList<Annotation>();
		Annotation ann;
		Iterator<Annotation> iter = this.iterator(feat);
		while ((ann = iter.next()) != null) {
			annotations.add(ann);
		}
		return annotations;
	}
	
	@Override
	public Iterator<Annotation> iterator() {

		String sql = "SELECT a.feat_name, i.com_name, i.gene_sym, i.ec# FROM asm_feature a, ident i, stan s" +
		" WHERE a.feat_name = i.feat_name AND s.asmbl_id = a.asmbl_id" +
		" AND s.iscurrent=" + this.isCurrent;
		
		return iteratorBySQL(sql);
	}
	
	public Iterator<Annotation> iterator(Feature feature) {
		return iterator(feature.getFeatureId());
	}
	
	public Iterator<Annotation> iterator(String featureId) {
		
		String sql = "SELECT a.feat_name, i.com_name, i.gene_sym, i.ec# FROM asm_feature a, ident i, stan s" +
				" WHERE a.feat_name = i.feat_name AND s.asmbl_id = a.asmbl_id" +
				" AND s.iscurrent=" + this.isCurrent +
				" AND a.feat_name='" + featureId + "'";
		
		return iteratorBySQL(sql);
	}
	
	public Iterator<Annotation> iteratorBySQL(String sql) {

		try {
			Statement stmt = conn.createStatement();
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
		
		// Use an anonymous inner class to return an Iterator of Annotation objects
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
						String featureId = rs.getString(1);
						nextAnnot.setCommonName(rs.getString(2));
						nextAnnot.setGeneSymbol(rs.getString(3));
						nextAnnot.setEcNumbers(rs.getString(4));
						
						// Add Go Terms to Annotation
						List<String> goIds = getGoIds(featureId);
						nextAnnot.addGoIds(goIds);						
						
						// Add RoleIds to Annotation
						List<String> roleIds = getRoleIds(featureId);
						nextAnnot.addRoleIds(roleIds);
						
						return nextAnnot;
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}
			public void remove() {
				throw new UnsupportedOperationException("no remove allowed from Annotation Iterator");
			}
		};
	}
	
	// GO Terms
	public List<String> getGoIds(String featureId) {
		
		String sql = "SELECT go.go_id " +
			"FROM go_role_link as go " +
			"JOIN asm_feature as a ON a.feat_name = go.feat_name " +
			"JOIN stan as s ON s.asmbl_id = a.asmbl_id " +
			"AND s.iscurrent=" + isCurrent +
			" AND go.feat_name='" + featureId + "'";
		return getGoIdsBySQL(sql);		
	}
	public List<String> getGoIdsBySQL(String sql) {

		ArrayList<String> goIds = new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement();
			final ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				goIds.add(rs.getString(1));
			}
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return goIds;	
	}
	
	// Role Ids
	public List<String> getRoleIds(String featureId) {
		String sql = "SELECT r.role_id FROM role_link r, asm_feature a, stan s" +
		" WHERE a.feat_name = r.feat_name AND s.asmbl_id = a.asmbl_id" +
		" AND s.iscurrent = " + isCurrent +
		" AND a.feat_name='" + featureId + "'";
		return getRoleIdsBySQL(sql);
	}
	
	public List<String> getRoleIdsBySQL(String sql) {
		ArrayList<String> roleIds = new ArrayList<String>();
		
		try {
			Statement stmt = conn.createStatement();
			final ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				roleIds.add(rs.getString(1));
			}
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return roleIds;	
	}
}
