package org.jcvi.annotation.facts;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Taxon {
	private int taxonId;
	private String name;
	private int parentTaxonId;
	private String rank;
		
	// Constructors
	public Taxon(int taxonId) {
		super();
		this.taxonId = taxonId;
	}

	public Taxon(int taxonId, String name) {
		this(taxonId);
		this.name = name;
	}

	// Getters and Setters
	public int getTaxonId() {
		return taxonId;
	}

	public void setTaxonId(int taxonId) {
		this.taxonId = taxonId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParentTaxonId() {
		return parentTaxonId;
	}

	public void setParentTaxonId(int parentTaxonId) {
		this.parentTaxonId = parentTaxonId;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
	
    /**
     * Returns the taxonomy hierarchy of this taxon entry in the form:
     *   most specific; less specific; ...; least specific.
     * It follows the chain up the tree as far as it can, and will stop
     * at the first one it comes to that returns null for getParentNCBITaxID().
     * If this taxon entry has no scientific name, you will get the string ".".
     * @return the display name as described above.
     */
    public List<Taxon> getHierarchy(TreeMap<Integer, Taxon> taxMap) {
    	List<Taxon> hier = new ArrayList<Taxon>();
     	hier.add(this);
    	
     	Taxon t = this;
     	while ((t = taxMap.get(t.getParentTaxonId())) != null) {
     		// Avoid endless loop if child's parent is itself
     		if (hier.contains(t)) break;
     		// Add parents to begin of list hierarchy
      		hier.add(t);
     	}
    	return hier;
    }

    public String getIdHierarchy(TreeMap<Integer, Taxon> taxMap) {
	   	List<Taxon> hier = this.getHierarchy(taxMap);
    	StringBuffer sb = new StringBuffer();
    	for (Taxon t : hier) {
    		sb.append(t.getTaxonId());
    		// Why Java is clunky...
    		if (hier.indexOf(t) < hier.size() - 1)
    			sb.append(" -> ");
    	}
    	return sb.toString();
    }
    public String getNameHierarchy(TreeMap<Integer, Taxon> taxMap) {
    	List<Taxon> hier = this.getHierarchy(taxMap);
    	StringBuffer sb = new StringBuffer();
    	for (Taxon t : hier) {
    		sb.append(t.getName());
    		if (hier.indexOf(t) < hier.size() - 1)
    			sb.append(" -> ");
    	}
    	return sb.append("root").toString();
    }
    
}
