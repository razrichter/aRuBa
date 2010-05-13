package org.jcvi.annotation.facts;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Genome {
	private Taxon taxon = new Taxon();
	private List<Property> properties = new ArrayList<Property>();
	
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
	
	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public void addProperty(Property prop) {
		this.properties.add(prop);
	}
	public void removeProperty(GenomeProperty p) {
		this.removeProperty(p.get("id").toString());
	}
	public void removeProperty(FeatureProperty p) {
		this.removeProperty(p.get("id").toString());
	}
	public void removeProperty(String id) {
		Map<String, Object> prop;
		for (int i=0; i < this.properties.size(); i++) {
			prop = this.properties.get(i);
			if (prop.containsKey("id") && prop.get("id").equals(id)) {
				this.properties.remove(i);
			}
		}
	}

}
