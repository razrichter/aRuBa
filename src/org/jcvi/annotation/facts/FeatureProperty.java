package org.jcvi.annotation.facts;

/*
 * This is a flyweight class for caching equivalent FeatureProperty objects
 */
public class FeatureProperty extends Property {
	
	protected FeatureProperty(String id) {
		super(id);
	}
	public static FeatureProperty create(String id) {
		return FeaturePropertyFactory.create(id);
	}

	public boolean equals(Object p) {
		if (p.getClass() == this.getClass()) {
			FeatureProperty fp = (FeatureProperty) p;
			return fp.getId().equals(this.getId());
		}
		return false;
	}
	
	public int hashCode() {
		return this.getId().hashCode();
	}
	
	public String toString() {
		return this.getClass().getName() + "_" + getId();
	}
}
