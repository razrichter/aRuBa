package org.jcvi.annotation.facts;

import java.util.HashMap;

public interface Relationship {

	Object getSubject();
	void setSubject(HashMap<String, Object> subject);

	Object getObject();
	void setObject(HashMap<String, Object> object);
	
	RelationshipType getType();
	void setType(RelationshipType type);
	
}
