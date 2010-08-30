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

	public static synchronized GenomeProperty create(String id, String name,
			String type) {
		GenomeProperty p = create(id);
		p.setName(name);
		p.setType(type);
		return p;
	}

	public static GenomeProperty get(String id) {
		return (GenomeProperty) propsCache.get(id);
	}
	public static boolean has(String id) {
		return propsCache.containsKey(id);
	}
	
	public static synchronized Collection<GenomeProperty> getProperties() {
		return propsCache.values();
	}

	public static GenomeProperty getRootProperty() {
		for (GenomeProperty p : getProperties()) {
			if (p.getType().equals("root")) {
				return p;
			}
		}
		return null;
	}

	public static void clearCache() {
		propsCache.clear();
	}
}
