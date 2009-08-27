package org.jcvi.annotation.facts;
import java.util.HashMap;

public class GenomeProperty extends HashMap<String, Object> {

	public GenomeProperty() {
		super();
	}
	public GenomeProperty(Object id) {
		super();
		this.put("id", id);
	}
}

