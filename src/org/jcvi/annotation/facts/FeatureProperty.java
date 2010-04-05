package org.jcvi.annotation.facts;
import java.util.HashMap;

public class FeatureProperty extends HashMap<String, Object> {

	public FeatureProperty() {
		super();
	}
	public FeatureProperty(Object id) {
		super();
		this.put("id", id);
	}
}
