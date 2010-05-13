package org.jcvi.annotation.facts;

import java.util.HashMap;

public abstract class Property extends HashMap<String, Object> {

	private String id;
	private double threshold;
	private double value;
	private PropertyState state;
	
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

	public PropertyState getState() {
		return state;
	}
	public void setState(PropertyState state) {
		this.state = state;
	}
	public void setStateYes() {
		this.state = PropertyState.YES;
	}
}
