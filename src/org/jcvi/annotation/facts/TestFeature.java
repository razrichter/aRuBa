package org.jcvi.annotation.facts;

import java.util.ArrayList;
import java.util.List;

public class TestFeature {
	private String featureId;
	private List<TestFeatureProperty> properties = new ArrayList<TestFeatureProperty>();
		
	public TestFeature(String featureId) {
		super();
		this.featureId = featureId;
	}

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public List<TestFeatureProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<TestFeatureProperty> properties) {
		this.properties = properties;
	}
	public void addProperty(TestFeatureProperty prop) {
		this.properties.add(prop);
	}

}
