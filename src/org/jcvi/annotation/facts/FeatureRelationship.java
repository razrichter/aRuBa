package org.jcvi.annotation.facts;

public class FeatureRelationship extends Relationship {

	protected Feature subject;
	protected Feature object;
	
	public FeatureRelationship(Feature subject, RelationshipType type, Feature object) {
		super(subject, type, object);
	}

	public Feature getSubject() {
		return subject;
	}
	public void setSubject(Feature subject) {
		this.subject = subject;
	}
	public Feature getObject() {
		return object;
	}
	public void setObject(Feature object) {
		this.object = object;
	}
}
