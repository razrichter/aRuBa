package org.jcvi.annotation.facts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Relationship {

	protected Object subject;
	protected Object object;
	protected String type;
	protected List<Map<String, Object>> properties = new ArrayList<Map<String, Object>>();
	
	public Relationship(Object subject, Object object, String type) {
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
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
