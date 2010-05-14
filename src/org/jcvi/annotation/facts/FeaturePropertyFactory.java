package org.jcvi.annotation.facts;

import java.util.Collection;
import java.util.HashMap;

public abstract class FeaturePropertyFactory {
	
	private static HashMap<String, FeatureProperty> propsCache = new HashMap<String, FeatureProperty>();

	public static FeatureProperty create(String id) {
		FeatureProperty p = (FeatureProperty) propsCache.get(id);
		if (p == null) {
			p = new FeatureProperty(id);
			propsCache.put(id, p);
		}
		return p;
	}

	public static Collection<FeatureProperty> getProperties() {
		return propsCache.values();
	}

}
