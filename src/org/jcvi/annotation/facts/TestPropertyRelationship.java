package org.jcvi.annotation.facts;

public class TestPropertyRelationship {

	private TestFeatureProperty subject;
	private TestFeatureProperty object;
	private RelationshipType type;
	
	public TestPropertyRelationship(TestFeatureProperty subject, RelationshipType type, TestFeatureProperty object) {
		super();
		this.setSubject(subject);
		this.setType(type);
		this.setObject(object);
	}

	public TestFeatureProperty getSubject() {
		return subject;
	}
	public void setSubject(TestFeatureProperty subject) {
		this.subject = subject;
	}

	public RelationshipType getType() {
		return type;
	}
	public void setType(RelationshipType type) {
		this.type = type;
	}
	
	public TestFeatureProperty getObject() {
		return object;
	}
	public void setObject(TestFeatureProperty object) {
		this.object = object;
	}
}
