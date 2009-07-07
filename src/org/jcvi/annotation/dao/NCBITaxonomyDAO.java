package org.jcvi.annotation.dao;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.jcvi.annotation.facts.Taxon;

public class NCBITaxonomyDAO implements TaxonomyDAO {

	private URL namesUrl;
	private URL nodesUrl;
	private String nameClassFilter;
	private TreeMap<Integer, Taxon> taxonomyMap = new TreeMap<Integer, Taxon>();

	public NCBITaxonomyDAO() {
		super();
	}

	public NCBITaxonomyDAO(URL namesUrl, URL nodesUrl) {
		this();
		this.namesUrl = namesUrl;
		this.nodesUrl = nodesUrl;
	}
	
	// Getters and Setters
	public URL getNamesUrl() {
		return namesUrl;
	}

	public void setNamesUrl(URL namesUrl) {
		this.namesUrl = namesUrl;
	}

	public URL getNodesUrl() {
		return nodesUrl;
	}

	public void setNodesUrl(URL nodesUrl) {
		this.nodesUrl = nodesUrl;
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

	/* 
	 * Load the complete taxonomy
	 */
	@Override
	public TreeMap<Integer, Taxon> loadTaxonomyMap() {
		loadNames();
		loadNodes();
		return taxonomyMap;
	}
	
	@Override
	public TreeMap<Integer, Taxon> loadTaxonomyMap(String taxonName) {
		Taxon taxon = getTaxon(taxonName);
		return this.loadTaxonomyMap(taxon);
	}
	
	// Load the taxonomy map for this taxon
	public TreeMap<Integer, Taxon> loadTaxonomyMap(Taxon taxon) {
		
		// Get the taxon and its relatives (parents only for now)
		List<Taxon> parents = this.getParents(taxon);
		
		// Empty our taxonomy map
		taxonomyMap.clear();
		
		// Re-create our new taxonomy with only the taxon and its parents
		for (Taxon p : parents) {
			taxonomyMap.put(new Integer(p.getTaxonId()), p);
		}
		return taxonomyMap;
	}

	// By default, we load all nodes
	public void loadNodes() {
		loadNodes(true);
	}
	
	public TreeMap<Integer, Taxon> loadNodes(boolean addIfNotFound) {
 
        try {
            BufferedReader nodesRdr = new BufferedReader(new InputStreamReader(
					nodesUrl.openStream()));

            String line;
            while ((line = nodesRdr.readLine()) != null) {
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
		    		taxonomyMap.put(tax_id, t);
			    }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return taxonomyMap;
	}
	
	// By default, we load all names of the specified class
	public void loadNames() {
		loadNames(false);
	}
	
	// Option: load only taxon names of reqNameClass
	public void loadNames(boolean addSynonyms) {
        try {
            BufferedReader namesRdr = new BufferedReader(new InputStreamReader(
					namesUrl.openStream()));

            String line;
			while ((line = namesRdr.readLine()) != null) {
	            String[] parts = line.split("\\|");
	            Integer tax_id = Integer.valueOf(parts[0].trim());
	            String name = parts[1].trim();
	            String nameClass = parts[3].trim();
	            
	            // Get the taxon object
	            Taxon t;
	            if ((t = taxonomyMap.get(tax_id)) == null) {
	            	t = new Taxon(tax_id);
	            	taxonomyMap.put(tax_id, t);
	            }
	            
			    // Set the scientific name and/or synonyms
			    if (nameClass.equals("scientific name")) {
			    	// t.setName(name);
				} else if (addSynonyms) {
					// t.addName(name);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Taxon> getParents(Taxon t) {
		// Requires loading the taxonomy map
		if (taxonomyMap.size() == 0)
			taxonomyMap = this.loadTaxonomyMap();
		
		// Build a list of its parent taxon objects
    	List<Taxon> parents = new ArrayList<Taxon>();

    	// We need to set the parent
    	Taxon p = t;
    	if (p.getParentTaxonId() == 0) 
    		p.setParentTaxonId(taxonomyMap.get(new Integer(p.getTaxonId())).getParentTaxonId());

      	while ((p = taxonomyMap.get(p.getParentTaxonId())) != null) {
     		
     		// Avoid endless loop if child's parent is itself
     		if (parents.contains(p)) break;
     		
     		// Add parents to begin of list hierarchy
     		parents.add(p);
     	}
     	t.setParents(parents);
     	return parents;		
	}
	
	public Taxon setTaxonParentId(Taxon t) {
        try {
            BufferedReader nodesRdr = new BufferedReader(new InputStreamReader(
					nodesUrl.openStream()));

            String line;
			while ((line = nodesRdr.readLine()) != null) {
			    String[] parts = line.split("\\|");
			    Integer tax_id = Integer.valueOf(parts[0].trim());
			    String pti = parts[1].trim();
			    Integer parent_tax_id = (pti.length() > 0 && pti != "all")? new Integer(pti) : null;

			    if (t.getTaxonId() == tax_id) {
			    	t.setParentTaxonId(parent_tax_id);
			    	return t;
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public Taxon getTaxon(String taxonName) {
        try {
            BufferedReader namesRdr = new BufferedReader(new InputStreamReader(
					namesUrl.openStream()));

            String line;
			while ((line = namesRdr.readLine()) != null) {
	            String[] parts = line.split("\\|");
	            String name = parts[1].trim();
	            Integer taxonId = Integer.valueOf(parts[0].trim());
	            if (name.equalsIgnoreCase(taxonName.trim()))
	            	return new Taxon(taxonId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Taxon getTaxon(int taxonId) {
        try {
            BufferedReader namesRdr = new BufferedReader(new InputStreamReader(
					namesUrl.openStream()));

            String line;
			while ((line = namesRdr.readLine()) != null) {
	            String[] parts = line.split("\\|");
	            String name = parts[1].trim();
	            int id = Integer.parseInt(parts[0].trim());
	            if (id == taxonId) 
	            	return new Taxon(new Integer(id), name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}


