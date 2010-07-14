package org.jcvi.annotation.dao;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.annotation.facts.Property;

public interface PropertyDAO extends Iterable<Property> {
	Property getProperty(String propId);
	Iterator<Property> iterator();
}


