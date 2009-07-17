package org.jcvi.annotation.dao;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.jcvi.annotation.facts.Taxon;

public class SmallGenomeTaxonomyDAO extends GenericTaxonomyDAO {

	private Connection conn;

	// Constructors
	public SmallGenomeTaxonomyDAO(Connection conn) {
		super();
		this.conn = conn;
	}

	// Getters & Setters
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
	public Map<Integer, Taxon> loadTaxonomyMap() {
		return loadTaxonomyMap(false);
	}

	public Taxon getTaxon(String taxonName) throws DaoException {
		String sql = "SELECT n.tax_id, n.parent_tax_id, s.name, n.rank " +
		"FROM gb_nodes n, gb_names s WHERE n.tax_id = s.tax_id" +
		" AND s.name='" + taxonName + "'";
		if (this.nameClassFilter != null) {
			sql += " AND s.name_class='" + this.nameClassFilter + "'";
		}
		return this.getTaxonBySQL(sql);
	}
	
	public Taxon getTaxon(Integer taxonId) throws DaoException {
		if (taxonomyMap.get(taxonId) instanceof Taxon)
			return taxonomyMap.get(taxonId);
		
		String sql = "SELECT n.tax_id, n.parent_tax_id, s.name, n.rank " +
		"FROM gb_nodes n, gb_names s WHERE n.tax_id = s.tax_id" +
		" AND s.tax_id=" + taxonId;
		if (this.nameClassFilter != null) {
			sql += " AND s.name_class='"+ this.nameClassFilter + "'";
		}
		return this.getTaxonBySQL(sql);
	}
	public Taxon getTaxonBySQL(String sql) throws DaoException {
		Taxon taxon = null;
		System.out.println(sql);
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				Integer taxonId = Integer.valueOf(rs.getInt(1));
				Integer parentTaxonId = Integer.valueOf(rs.getInt(2));
				String name = rs.getString(3);
				System.out.println("taxonId:"+taxonId+", name:"+name);
				taxon = new Taxon(taxonId, name);
				taxon.setParent(new Taxon(parentTaxonId));
				// taxon.setRank(rs.getString(4));
			} else {
				throw new DaoException("Taxon not found.");
			}
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			for (Throwable t : e) {
				t.printStackTrace();
			}
		}
		return taxon;
	}

	public Map<Integer, Taxon> loadTaxonomyMap(boolean addSynonyms) {

		Connection conn = this.getConn(); 
		String sql = "SELECT n.tax_id, n.parent_tax_id, s.name, n.rank " +
				"FROM gb_nodes n, gb_names s WHERE n.tax_id = s.tax_id";
		
		if (this.nameClassFilter != null) {
			sql += " AND s.name_class='"+ this.nameClassFilter + "'";
		}
		
		Taxon t; Taxon p;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Integer taxonId = Integer.valueOf(rs.getInt(1));
				Integer parentTaxonId = Integer.valueOf(rs.getInt(2));
				String name = rs.getString(3);
				String nameClass = rs.getString(4);
				
	            // Get the taxon object
	            if ((t = taxonomyMap.get(taxonId)) == null) {
	            	t = new Taxon(taxonId);
	            	taxonomyMap.put(taxonId, t);
	            }
	            
			  	// Get the parent Taxon
	            if ((p = taxonomyMap.get(parentTaxonId)) == null) {
	            	p = new Taxon(parentTaxonId);
	            	taxonomyMap.put(parentTaxonId, p);
	            }

			    // Set the scientific name and/or synonyms
			    if (nameClassFilter == null || 
			    		nameClass.equals(this.nameClassFilter)) {
			    	t.setName(name);
				} else if (addSynonyms) {
					// t.addName(name);
				}
			}
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			for (Throwable tr : e) {
				tr.printStackTrace();
			}
		}
		return taxonomyMap;
	}	
	
	
	
}
