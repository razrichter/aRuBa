package org.jcvi.annotation.facts;

public class PropertyRelationship {

	private Property subject;
	private Property object;
	private RelationshipType type;
	
	public PropertyRelationship(Property subject, RelationshipType type, Property object) {
		super();
		this.setSubject(subject);
		this.setType(type);
		this.setObject(object);
		
		// Store bi-directional relationships
		// subject -- type --> object and object -- reverse type --> subject
		// Ex. FeatureProperty_101 REQUIRED_BY GenomeProperty_2029 
		subject.addParentRelationship(type, object);
		object.addChildRelationship(type, subject);
	}

	public Property getSubject() {
		return subject;
	}
	public void setSubject(Property subject) {
		this.subject = subject;
	}

	public RelationshipType getType() {
		return type;
	}
	public void setType(RelationshipType type) {
		this.type = type;
	}
	
	public Property getObject() {
		return object;
	}
	public void setObject(Property object) {
		this.object = object;
	}
	public String toString() {
		return this.subject.toString() + " " + this.getType().toString() + " " + this.object.toString();
	}
}
