package org.jcvi.annotation.facts;
import java.util.ArrayList;
import java.util.List;

public class Taxon {
	private int taxonId;
	private String name; // Scientific name
	private List<String> names = new ArrayList<String>();
	private Taxon parent;
	private List<Taxon> children;
	private String rank;
		
	// Constructors
	public Taxon() {
		super();
	}
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
	public List<String> getNames() {
		return names;
	}
	public void addName(String name) {
		this.names.add(name);
	}
	public void setNames(List<String> names) {
		this.names = names;
	}
	
	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
	
    public Taxon getParent() {
		return parent;
	}
	public void setParent(Taxon parent) {
		this.parent = parent;
	}
	public List<Taxon> getChildren() {
		return children;
	}
	public void setChildren(List<Taxon> children) {
		this.children = children;
	}
	public List<Taxon> getParents() {
    	List<Taxon> parents = new ArrayList<Taxon>();
    	
    	Taxon p = this;
      	while ((p = p.getParent()) != null) {
     		
      		// Avoid endless loop if child's parent is itself
     		if (parents.contains(p)) break;
     		
     		parents.add(p);
     	}
      	return parents;
	}

    public List<Integer> getParentIds() {
    	List<Taxon> parents = this.getParents();
    	List<Integer> parentIds = new ArrayList<Integer>();
    	for (Taxon p : parents) {
    		parentIds.add(Integer.valueOf(p.getTaxonId()));
    	}
    	return parentIds;
    }
    public List<String> getParentNames() {
    	List<Taxon> parents = this.getParents();
    	List<String> parentNames = new ArrayList<String>();
    	for (Taxon p : parents) {
    		parentNames.add(p.getName());
    	}
    	return parentNames;
    }
}
