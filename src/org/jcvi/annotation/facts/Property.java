package org.jcvi.annotation.facts;

import java.util.HashMap;

public abstract class Property {

	private String id;
	private double threshold;
	private double value;
	private double filled;
	private double required;
	private String definition;
	private HashMap<String, Object> attributes = new HashMap<String, Object>();
	
	protected Property(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public void setThreshold(String threshold) {
		this.threshold = new Double(threshold);
	}

	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public void setValue(String value) {
		this.value = new Double(value);
	}

	public double getFilled() {
		return filled;
	}
	public void setFilled(double filled) {
		this.filled = filled;
	}
	public double getRequired() {
		return required;
	}
	public void setRequired(double required) {
		this.required = required;
	}

	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public HashMap<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(HashMap<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	
}
