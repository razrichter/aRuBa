package org.jcvi.annotation.facts;
import java.util.Collection;
import java.util.HashMap;

/*
 * This is a flyweight class for caching equivalent FeatureProperty objects
 */
public class FeatureProperty extends HashMap<String, Object> {
	
	private static HashMap<String, FeatureProperty> propsCache = new HashMap<String, FeatureProperty>();
	
	private FeatureProperty() {
		super();
	}
	private FeatureProperty(String id) {
		super();
		this.put("id", id);
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
			return p.get("id").equals(this.get("id"));
		}
		return false;
	}
	
	public int hashCode() {
		return this.get("id").hashCode();
	}
}
