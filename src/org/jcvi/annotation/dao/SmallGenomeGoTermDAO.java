package org.jcvi.annotation.dao;
import java.sql.*;
import java.util.Iterator;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.GoTerm;

public class SmallGenomeGoTermDAO implements GoTermDAO {

	private Connection conn;
	private int isCurrent = 1;
	
	public SmallGenomeGoTermDAO() {
		// Any initialization
	}
	
	public SmallGenomeGoTermDAO(Connection conn) {
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
	public GoTerm getGoTerm(String goTermId) {
		return null;
	}

	@Override
	public Iterator<GoTerm> iterator() {

		String sql = "SELECT go.feat_name, go.go_id, go.qualifier, ev.ev_code, ev.evidence, ev.with_ev, ev.secondary_NCBI_taxon_id" +
				" FROM go_role_link go, go_evidence ev, asm_feature a" +
				" WHERE go.id = ev.role_link_id AND a.feat_name = go.feat_name" +
				" AND s.asmbl_id = a.asmbl_id" +
				" AND s.iscurrent = " + this.isCurrent;
		
		return iteratorBySQL(sql);
	}

	public Iterator<GoTerm> iterator(Feature feature) {
		
		String sql = "SELECT go.go_id, go.qualifier, ev.ev_code, ev.evidence, ev.with_ev, ev.secondary_NCBI_taxon_id" +
		" FROM go_role_link go, go_evidence ev, asm_feature a" +
		" WHERE go.id = ev.role_link_id AND a.feat_name = go.feat_name" +
		" AND s.asmbl_id = a.asmbl_id" +
		" AND s.iscurrent = " + this.isCurrent +
		" AND a.feat_id=" + feature.getFeatureId();
		
		return iteratorBySQL(sql);
	}
	
	public Iterator<GoTerm> iteratorBySQL(String sql) {

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			final ResultSet rs = stmt.executeQuery(sql);
			return this.getGoTermIterator(rs);
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return null;	
	}

	public Iterator<GoTerm> getGoTermIterator(final ResultSet rs) {
		
		// Use an anonymous inner class to return an Iterator of Feature objects
		return new Iterator<GoTerm>() {	
			private GoTerm term = null;
			
			public boolean hasNext() {
				// Get/Set the next annotation object
				if (term == null) term = next();
				return (term == null) ? false : true;
			}
			public GoTerm next() {
				
				if (term != null) {
					GoTerm goTermTmp = term;
					term = null;
					return goTermTmp;
				}
				
				try {
					if (rs.next()) {
						GoTerm nextGoTerm = new GoTerm(rs.getString(1));
						nextGoTerm.setAccessionCode(rs.getString(2));
						return nextGoTerm;
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}
			public void remove() {
				throw new UnsupportedOperationException("no remove allowed from GoTerm Iterator");
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
