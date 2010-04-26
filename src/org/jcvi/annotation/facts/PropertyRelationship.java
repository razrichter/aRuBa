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
}
