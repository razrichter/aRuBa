package org.jcvi.annotation.facts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FeatureTypeTable {
	
	private static Map<String, Integer> typesMapByName = new HashMap<String, Integer>();
	private static Map<Integer, String> typesMapById = new HashMap<Integer, String>();
	
	static {
		int ids[] = { 1, 2 };
		String names[] = { "ORF", "tRNA" };
		for (int i = 0; i < ids.length; i++) {
			typesMapByName.put(names[i], Integer.valueOf(i));
			typesMapById.put(Integer.valueOf(i), names[i]);
		}
	}

	public Collection<String> nameEntrySet() {
		return typesMapById.values();
	}
	public Collection<Integer> idEntrySet() {
		return typesMapByName.values();
	}
	public String getNameById(Integer id) {
		return typesMapById.get(id);
	}
	public Integer getIdByName(String name) {
		return typesMapByName.get(name);
	}
	public boolean addType(String name, Integer id) {
		if (typesMapByName.containsKey(name) || typesMapById.containsKey(id)) {
			return false;
		} else {
			typesMapByName.put(name, id);
			typesMapById.put(id, name);
			return true;
		}
	}

}
