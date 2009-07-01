package org.jcvi.annotation.dao;
import java.io.BufferedReader;
import java.util.TreeMap;
import org.jcvi.annotation.facts.Taxon;

/**
 * @author naxelrod
 *
 */
public class NCBITaxonomyDAO implements TaxonomyDAO {

	private BufferedReader namesRdr;
	private BufferedReader nodesRdr;
	private String nameClassFilter;
	private TreeMap<Integer, Taxon> taxonomyMap = new TreeMap<Integer, Taxon>();

	public NCBITaxonomyDAO() {
		super();
	}

	public NCBITaxonomyDAO(BufferedReader namesRdr, BufferedReader nodesRdr) {
		this();
		this.namesRdr = namesRdr;
		this.nodesRdr = nodesRdr;
	}
	
	// Getters and Setters
    public BufferedReader getNamesRdr() {
		return namesRdr;
	}
	public void setNamesRdr(BufferedReader namesRdr) {
		this.namesRdr = namesRdr;
	}
	public BufferedReader getNodesRdr() {
		return nodesRdr;
	}
	public void setNodesRdr(BufferedReader nodesRdr) {
		this.nodesRdr = nodesRdr;
	}
	public String getNameClassFilter() {
		return nameClassFilter;
	}
	public void setNameClassFilter(String nameClassFilter) {
		this.nameClassFilter = nameClassFilter;
	}

	public TreeMap<Integer, Taxon> getTaxonomyMap() {
		return taxonomyMap;
	}

	public void setTaxonomyMap(TreeMap<Integer, Taxon> taxonomyMap) {
		this.taxonomyMap = taxonomyMap;
	}

	public TreeMap<Integer, Taxon> loadTaxonomyMap() {
		loadNames(nameClassFilter);
		loadNodes(false);
		return this.getTaxonomyMap();
	}
	
	// By default, we load all nodes
	public void loadNodes() {
		loadNodes(true);
	}
	
	public void loadNodes(boolean addIfNotFound) {
        String line;
        try {
			while ((line = this.nodesRdr.readLine()) != null) {
			    String[] parts = line.split("\\|");
			    Integer tax_id = Integer.valueOf(parts[0].trim());
			    String pti = parts[1].trim();
			    Integer parent_tax_id = (pti.length()>0 && pti != "all")?new Integer(pti):null;
			    
			    Taxon t;
			    if ((t = taxonomyMap.get(tax_id)) != null) {
			    	t.setParentTaxonId(parent_tax_id);
			    	
			    } else if (addIfNotFound) {
		    		t = new Taxon(tax_id);
		    		t.setParentTaxonId(parent_tax_id);
		    		taxonomyMap.put(new Integer(t.getTaxonId()), t);
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// No nameClass filter by default
	public void loadNames() {
		loadNames(null);
	}
	
	// Option: load only taxon names of reqNameClass
	public void loadNames(String reqNameClass) {
        String line;
        try {
			while ((line = this.namesRdr.readLine()) != null) {
	            String[] parts = line.split("\\|");
	            Integer tax_id = Integer.valueOf(parts[0].trim());
	            String name = parts[1].trim();
	            String nameClass = parts[3].trim();
	            // Only store Taxons of the user specified name class
	            if (reqNameClass == null || nameClass.equals(reqNameClass)) {
				    
				    Taxon t;
				    if ((t = taxonomyMap.get(tax_id)) == null) {
				    	// Memory problems with load taxonomies
				    	// t = new Taxon(tax_id, name);
				    	
				    	t = new Taxon(tax_id);
				    	taxonomyMap.put(new Integer(t.getTaxonId()), t);
				    } else if (t.getName() != name) {
				    	t.setName(name);
				    }
	            	
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


