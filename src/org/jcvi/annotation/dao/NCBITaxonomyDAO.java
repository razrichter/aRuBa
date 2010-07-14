package org.jcvi.annotation.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import org.jcvi.annotation.facts.Taxon;

import com.sun.xml.internal.ws.Closeable;

public class NCBITaxonomyDAO extends GenericTaxonomyDAO {

	private URL namesUrl;
	private URL nodesUrl;

	public NCBITaxonomyDAO(URL namesUrl, URL nodesUrl) {
		super();
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

	@Override
	public Map<Integer, Taxon> loadTaxonomyMap() {
		loadNames();
		loadNodes();
		return taxonomyMap;
	}

	public void loadNodes() {

		// Make sure the names are loaded first
		if (taxonomyMap.size() == 0) this.loadNames();

		BufferedReader nodesRdr = null;
		try {
			nodesRdr = new BufferedReader(new InputStreamReader(nodesUrl.openStream()));

			String line;
			while ((line = nodesRdr.readLine()) != null) {
				String[] parts = line.split("\\|");
				Integer taxonId = Integer.valueOf(parts[0].trim());
				String pti = parts[1].trim();
				Integer parentTaxonId = (pti.length() > 0 && !(pti.equals("all"))) ? new Integer(pti) : null;

				// Get our Taxon object
				Taxon t = taxonomyMap.get(taxonId);

				// Get our parent Taxon
				Taxon p = taxonomyMap.get(parentTaxonId);

				// Set the parent
				t.setParent(p);
			}
			nodesRdr.close();

		} catch (IOException e) {
			e.printStackTrace();
		
		} finally {
			this.close(nodesRdr);
		}
	}

	// By default, we load all names of the specified class
	public void loadNames() {
		loadNames(false);
	}

	// Option: load only taxon names of reqNameClass
	public void loadNames(boolean addSynonyms) {
		BufferedReader namesRdr = null;
		try {
			namesRdr = new BufferedReader(new InputStreamReader(
					namesUrl.openStream()));

			String line;
			while ((line = namesRdr.readLine()) != null) {
				String[] parts = line.split("\\|");
				Integer taxonId = Integer.valueOf(parts[0].trim());
				String name = parts[1].trim();
				String nameClass = parts[3].trim();

				// Get the taxon object
				Taxon t;
				if ((t = taxonomyMap.get(taxonId)) == null) {
					t = new Taxon(taxonId);
					taxonomyMap.put(taxonId, t);
				}

				// Set the scientific name and/or synonyms
				if (nameClass.equals("scientific name")) {
					t.setName(name);
				} else if (addSynonyms) {
					// t.addName(name);
				}
			}
			namesRdr.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(namesRdr);
		}
	}

	public Taxon setParentTaxon(Taxon t) {
		BufferedReader nodesRdr = null;
		try {
			nodesRdr = new BufferedReader(new InputStreamReader(nodesUrl.openStream()));
			String line;
			while ((line = nodesRdr.readLine()) != null) {
				String[] parts = line.split("\\|");
				Integer taxonId = Integer.valueOf(parts[0].trim());
				String pti = parts[1].trim();
				Integer parentTaxonId = (pti.length() > 0 && !(pti.equals("all"))) 
							? new Integer(pti) : null;

				if (t.getTaxonId() == taxonId) {

					// Get our parent Taxon
					Taxon p = taxonomyMap.get(parentTaxonId);
					if (p == null)
						p = new Taxon(parentTaxonId);

					t.setParent(p);
					return t;
				}
			}
			nodesRdr.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			this.close(nodesRdr);
		}
		return t;
	}

	public void close (BufferedReader stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Taxon getTaxon(String taxonName) {

		BufferedReader namesRdr = null;
		try {
			namesRdr = new BufferedReader(new InputStreamReader(
					namesUrl.openStream()));

			String line;
			while ((line = namesRdr.readLine()) != null) {
				String[] parts = line.split("\\|");
				String name = parts[1].trim();
				Integer taxonId = Integer.valueOf(parts[0].trim());
				if (name.equalsIgnoreCase(taxonName.trim()))
					return new Taxon(taxonId, name);
			}
			namesRdr.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			this.close(namesRdr);
		}
		return null;
	}

	public Taxon getTaxon(int taxonId) {
		BufferedReader namesRdr = null;
		try {
			namesRdr = new BufferedReader(new InputStreamReader(
					namesUrl.openStream()));

			String line;
			while ((line = namesRdr.readLine()) != null) {
				String[] parts = line.split("\\|");
				String name = parts[1].trim();
				int id = Integer.parseInt(parts[0].trim());
				if (id == taxonId)
					return new Taxon(Integer.valueOf(id), name);
			}
			namesRdr.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		
		} finally {
			this.close(namesRdr);
		}

		return null;
	}

}
