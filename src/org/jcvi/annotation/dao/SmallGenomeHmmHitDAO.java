package org.jcvi.annotation.dao;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.jcvi.annotation.facts.HmmHit;

public class SmallGenomeHmmHitDAO implements HmmHitDAO {

	private Connection conn;
	
	public SmallGenomeHmmHitDAO() {
	}
	public SmallGenomeHmmHitDAO(Connection conn) {
		this();
		this.conn = conn;
	}

	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getHmmHitSQL() {
		return "SELECT DISTINCT a.feat_name, e.accession, " +
			"e.rel_end5, e.rel_end3, e.m_lend, e.m_rend, f_total.score, f_domain.score " +
			"FROM evidence AS e " +
			"JOIN feat_score AS f_domain " +
			"ON e.id = f_domain.input_id and f_domain.score_id = 51 " +
			"JOIN feat_score AS f_total " +
			"ON e.id = f_total.input_id AND f_total.score_id = 143 " +
			"JOIN asm_feature AS a " +
			"ON e.feat_name = a.feat_name " +
			"JOIN stan AS s " +
			"ON a.asmbl_id = s.asmbl_id and s.iscurrent = 1 " +
			"JOIN egad..hmm2 AS h ON h.hmm_acc = e.accession and " +
			"h.is_current = 1 " +
			"WHERE (convert(NUMERIC, f_domain.score) >= h.trusted_cutoff " +
			"OR ( " +
				"convert(NUMERIC, f_total.score) >= h.trusted_cutoff " +
				"AND convert(NUMERIC, f_domain.score) >= h.trusted_cutoff2" +
			")OR (" +
				"e.curated = 1 AND e.assignby != 'sgc3'" +
			"))";
	}
	
	public String getHmmHitByIdSQL(String hitId) {
		return getHmmHitSQL() + " AND e.accession='" + hitId + "'";
	}
	
	public HmmHit getHmmHit(String hitId) {
		Iterator<HmmHit> iter = iteratorBySQL(getHmmHitByIdSQL(hitId));
		return iter.next();
	}
	
	public Iterator<HmmHit> iteratorBySQL(String sql) {
		try {
			Statement stmt = conn.createStatement();
			final ResultSet rs = stmt.executeQuery(sql);
			return this.getHitIterator(rs);
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return null;	
	}
	public Iterator<HmmHit> iterator() {
		return iteratorBySQL(getHmmHitSQL());
	}

	public Iterator<HmmHit> getHitIterator(final ResultSet rs) {
		
		// Use an anonymous inner class to return an Iterator of Feature objects
		return new Iterator<HmmHit>() {	
			private HmmHit hit = null;
			
			public boolean hasNext() {
				// Get/Set the next feature object
				if (hit == null) hit = next();
				return (hit == null) ? false : true;
			}
			public HmmHit next() {
				
				if (hit != null) {
					HmmHit hitTmp = hit;
					hit = null;
					return hitTmp;
				}
				
				try {
					if (rs.next()) {
						String queryId = rs.getString(1); // a.feat_name
						String hitId = rs.getString(2); // evidence.accession
						
						int queryStart = rs.getInt(3);
						int queryEnd = rs.getInt(4);
						int hitStart = rs.getInt(5);
						int hitEnd = rs.getInt(6);
						double score = rs.getDouble(7);
						double domainScore = rs.getDouble(8);
						
						// convert from min, max to start, end, strand syntax
						int queryStrand = 1;
						if (queryStart > queryEnd) {
							int tmpEnd = queryStart;
							queryEnd = queryStart;
							queryStart = tmpEnd;
							queryStrand = -1;
						}
						int hitStrand = 1;
						if (hitStart > hitEnd) {
							int tmpEnd = hitStart;
							hitEnd = hitStart;
							hitStart = tmpEnd;
							hitStrand = -1;
						}
						HmmHit hit = new HmmHit(queryId, queryStart, queryEnd, queryStrand,
									hitId, hitStart, hitEnd, hitStrand);
						hit.setScore(score);
						hit.setDomainScore(domainScore);
						return hit;
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}
			public void remove() {
				throw new UnsupportedOperationException("no remove allowed from HmmHit Iterator");
			}
		};
	}
	
}
