package org.jcvi.annotation.facts;

import java.util.HashMap;

public class PropertyRelationship extends Relationship {

	protected HashMap<String, Object> subject;
	protected HashMap<String, Object> object;
	
	public PropertyRelationship(HashMap<String, Object> subject, RelationshipType type, HashMap<String, Object> object) {
		super(subject, type, object);
	}

	public HashMap<String, Object> getSubject() {
		return subject;
	}
	public void setSubject(HashMap<String, Object> subject) {
		this.subject = subject;
	}
	public HashMap<String, Object> getObject() {
		return object;
	}
	public void setObject(HashMap<String, Object> object) {
		this.object = object;
	}
}
