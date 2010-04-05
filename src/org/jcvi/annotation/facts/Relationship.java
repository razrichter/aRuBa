package org.jcvi.annotation.facts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Should this be an abstract class?
public class Relationship {

	protected Object subject;
	protected Object object;
	protected RelationshipType type;
	protected List<Map<String, Object>> properties = new ArrayList<Map<String, Object>>();
	
	public Relationship(Object subject, RelationshipType type, Object object) {
		super();
		this.setSubject(subject);
		this.setObject(object);
	}

	public Object getSubject() {
		return subject;
	}
	public void setSubject(Object subject) {
		this.subject = subject;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public RelationshipType getType() {
		return type;
	}
	public void setType(RelationshipType type) {
		this.type = type;
	}
	public List<Map<String, Object>> getProperties() {
		return properties;
	}

	public void setProperties(List<Map<String, Object>> properties) {
		this.properties = properties;
	}
	public void addProperty(Map<String, Object> property) {
		this.properties.add(property);
	}
}
