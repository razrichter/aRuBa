package org.jcvi.annotation.facts;

import java.util.HashMap;

public class FeatureRelationship {

	protected Feature subject;
	protected Feature object;
	protected RelationshipType type;
	
	public FeatureRelationship(Feature subject, RelationshipType type, Feature object) {
		super();
		this.setSubject(subject);
		this.setType(type);
		this.setObject(object);
	}

	public Feature getSubject() {
		return subject;
	}
	public void setSubject(Feature subject) {
		this.subject = subject;
	}
	public RelationshipType getType() {
		return type;
	}
	
	public void setType(RelationshipType type) {
		this.type = type;
	}

	public Feature getObject() {
		return object;
	}
	public void setObject(Feature object) {
		this.object = object;
	}
}
