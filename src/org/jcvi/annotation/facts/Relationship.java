package org.jcvi.annotation.facts;

import java.util.HashMap;

public interface Relationship {

	Object getSubject();
	void setSubject(Object subject);

	Object getObject();
	void setObject(Object object);
	
	RelationshipType getType();
	void setType(RelationshipType type);
	
}
