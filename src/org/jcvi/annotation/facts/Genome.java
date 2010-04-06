package org.jcvi.annotation.facts;
import java.util.Map;

public class Genome {
	private Taxon taxon = new Taxon();
	private Map<String, Object> properties;
	
	// Constructor
	public Genome(Taxon taxon) {
		super();
		this.taxon = taxon;
	}

	public Genome() {
		super();
	}

	public Taxon getTaxon() {
		return taxon;
	}
	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	public void addProperty(String key, Object value) {
		this.properties.put(key, value);
	}
	public void removeProperty(String key) {
		this.properties.remove(key);
	}
	
	public void addProperty(FeatureProperty p) {
		this.properties.put(p.get("id").toString(), p);
	}
	public void addProperty(GenomeProperty p) {
		this.properties.put(p.get("id").toString(), p);
	}
	public void removeProperty(GenomeProperty p) {
		this.properties.remove(p.get("id").toString());
	}
	public void removeProperty(FeatureProperty p) {
		this.properties.remove(p.get("id").toString());
	}
}
