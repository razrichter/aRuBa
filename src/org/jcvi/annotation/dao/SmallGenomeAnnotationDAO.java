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

	@Override
	public Iterator<Annotation> getAnnotations() {

		String sql = "SELECT a.feat_id, i.com_name, i.gene_sym, i.ec# FROM asm_feature a, ident i, stan s" +
		" WHERE a.feat_name = i.feat_name AND s.asmbl_id = a.asmbl_id" +
		" AND s.iscurrent=" + this.isCurrent;
		
		return getAnnotationsBySQL(sql);
	}

	public Iterator<Annotation> getAnnotations(Feature feature) {
		
		String sql = "SELECT a.feat_id, i.com_name, i.gene_sym, i.ec# FROM asm_feature a, ident i, stan s" +
				" WHERE a.feat_name = i.feat_name AND s.asmbl_id = a.asmbl_id" +
				" AND s.iscurrent=" + this.isCurrent +
				" AND a.feat_id=" + feature.getFeatureId();
		
		return getAnnotationsBySQL(sql);
	}
	
	public Iterator<Annotation> getAnnotationsBySQL(String sql) {

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
						nextAnnot.setCommonName(rs.getString(2));
						nextAnnot.setGeneSymbol(rs.getString(3));
						nextAnnot.setEcNumber(rs.getString(4));
						
						String featureId = rs.getString(1);
						
						// TODO: Add Go Terms to Annotation
						
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

	public List<String> getRoleIds(String featureName) {
		
		String sql = "SELECT r.role_id FROM role_link r, asm_feature a, stan s" +
				" WHERE a.feat_name = r.feat_name AND s.asmbl_id = a.asmbl_id" +
				" AND s.iscurrent = " + isCurrent +
				" AND a.feat_name='" + featureName + "'";

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
