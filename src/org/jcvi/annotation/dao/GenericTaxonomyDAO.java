package org.jcvi.annotation.dao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.annotation.facts.Taxon;

public abstract class GenericTaxonomyDAO implements TaxonomyDAO {

	public String nameClassFilter = "scientific name";
	public Map<Integer, Taxon> taxonomyMap = new HashMap<Integer, Taxon>();

	public GenericTaxonomyDAO() {
		super();
	}
	
	// Any GenericTaxonomyDAO must implement loadTaxonomyMap() 
	public Map<Integer, Taxon> loadTaxonomyMap() { 
		return null;
	}
	public boolean hasTaxonomyMap() {
		return (taxonomyMap != null && taxonomyMap.size() > 0);
	}
	public Iterator<Taxon> iterator() {
		if (this.hasTaxonomyMap() == false) this.loadTaxonomyMap();
		return taxonomyMap.values().iterator();
	}
	
	public String getNameClassFilter() {
		return nameClassFilter;
	}
	public void setNameClassFilter(String nameClassFilter) {
		this.nameClassFilter = nameClassFilter;
	}
	public Map<Integer, Taxon> getTaxonomyMap() {
		return taxonomyMap;
	}
	public void setTaxonomyMap(Map<Integer, Taxon> taxonomyMap) {
		this.taxonomyMap = taxonomyMap;
	}

	public boolean hasTaxon(Integer taxonId) {
		if (this.hasTaxonomyMap() == false) this.loadTaxonomyMap();
		return (taxonomyMap.get(taxonId) instanceof Taxon);
	}
	public boolean hasTaxon(Taxon t) {
		if (this.hasTaxonomyMap() == false) this.loadTaxonomyMap();
		return (taxonomyMap.get(t.getTaxonId()) instanceof Taxon);
	}
	public List<Taxon> getParents(Taxon t) throws DaoException {
		if (this.hasTaxonomyMap() == false) this.loadTaxonomyMap();
		
		List<Taxon> parents = new ArrayList<Taxon>();
		Taxon p = t;
		if (p.getParent() == null) {
			p = taxonomyMap.get(t.getTaxonId());
	    	if (p == null)
	    		throw new DaoException("Taxon " + t.getTaxonId() + " not found.");
	    	t.setParent(p.getParent());
		}
    	
      	while ((p = p.getParent()) != null) {
      		
      		// Avoid endless loop if child's parent is itself
     		if (parents.contains(p)) break;
     		
     		parents.add(p);
     	}		
		return parents;
	}
	

}


