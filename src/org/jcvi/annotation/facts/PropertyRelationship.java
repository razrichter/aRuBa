package org.jcvi.annotation.facts;

import java.util.HashMap;

public class PropertyRelationship implements Relationship {

	private HashMap<String, Object> subject;
	private HashMap<String, Object> object;
	private RelationshipType type;
	
	public PropertyRelationship(HashMap<String, Object> subject, RelationshipType type, HashMap<String, Object> object) {
		super();
		this.setSubject(subject);
		this.setType(type);
		this.setObject(object);
	}

	@Override
	public HashMap<String, Object> getSubject() {
		return subject;
	}
	@Override
	public void setSubject(HashMap<String, Object> subject) {
		this.subject = subject;
	}

	@Override
	public RelationshipType getType() {
		return type;
	}
	@Override
	public void setType(RelationshipType type) {
		this.type = type;
	}
	
	@Override
	public HashMap<String, Object> getObject() {
		return object;
	}
	@Override
	public void setObject(HashMap<String, Object> object) {
		this.object = object;
	}
}
