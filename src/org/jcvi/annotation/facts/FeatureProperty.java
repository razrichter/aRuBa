package org.jcvi.annotation.facts;
import java.util.Collection;
import java.util.HashMap;

/*
 * This is a flyweight class for caching equivalent FeatureProperty objects
 */
public class FeatureProperty extends Property {
	
	private static HashMap<String, FeatureProperty> propsCache = new HashMap<String, FeatureProperty>();
	
	public FeatureProperty(String id) {
		super(id);
	}
	public static FeatureProperty create(String id) {
		if (propsCache.containsKey(id)) {
			return propsCache.get(id);
		} else 
		{
			FeatureProperty p = new FeatureProperty(id);
			propsCache.put(id, p);
			return p;
		}
	}
	
	public static Collection<FeatureProperty> getProperties() {
		return propsCache.values();
	}

	public boolean equals(FeatureProperty p) {
		if (p instanceof FeatureProperty) {
			return p.getId().equals(this.getId());
		}
		return false;
	}
	
	public int hashCode() {
		return this.getId().hashCode();
	}
}
