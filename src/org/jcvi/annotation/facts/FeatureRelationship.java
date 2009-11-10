package org.jcvi.annotation.facts;

public class FeatureRelationship extends Relationship {

	protected Feature subject;
	protected Feature object;
	
	public FeatureRelationship(Feature subject, Feature object, String type) {
		super(subject, object, type);
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
