package org.jcvi.annotation.facts;

import java.util.Collection;
import java.util.HashMap;

public class GenomePropertyFactory {

	private static HashMap<String, GenomeProperty> propsCache = new HashMap<String, GenomeProperty>();

	public static synchronized GenomeProperty create(String id) {
		GenomeProperty p = (GenomeProperty) propsCache.get(id);
		if (p == null) {
			p = new GenomeProperty(id);
			propsCache.put(id, p);
		}
		return p;
	}

	public static synchronized Collection<GenomeProperty> getProperties() {
		return propsCache.values();
	}
	
	public static void clearCache() {
		propsCache.clear();
	}

}
