package org.jcvi.annotation.facts;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/*
 * This is a flyweight class for caching equivalent GenomeProperty objects
 */

public class GenomeProperty extends Property {

	private static HashMap<String, GenomeProperty> propsCache = new HashMap<String, GenomeProperty>();
	
	public GenomeProperty(String id) {
		super(id);
	}
	
	public static Collection<GenomeProperty> getProperties() {
		return propsCache.values();
	}
	public static GenomeProperty create(String id) {
		if (propsCache.containsKey(id)) {
			return propsCache.get(id);
		} else 
		{
			GenomeProperty p = new GenomeProperty(id);
			propsCache.put(id, p);
			return p;
		}
	}
	
	public boolean equals(GenomeProperty p) {
		if (p instanceof GenomeProperty) {
			return p.getId().equals(this.getId());
		}
		return false;
	}
	public int hashCode() {
		return this.getId().hashCode();
	}
}

