package org.jcvi.annotation.dao;
import java.util.Iterator;
import java.util.Map;

public interface PropertyDAO extends Iterable<Map<String, Object>> {
	Map<String, Object> getProperty(String propId);
	Iterator<Map<String, Object>> iterator();
}


