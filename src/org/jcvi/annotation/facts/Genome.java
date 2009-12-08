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
}
