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
		if (p instanceof FeatureProperty) {
			FeatureProperty fp = (FeatureProperty) p;
			if (fp.getId().equals(this.getId())) {
				return true;
			}
		}
		return false;
	}
	
	public int hashCode() {
		return this.getId().hashCode();
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + "." + getId();
		// return this.getClass().getName() + "_" + getId();
	}
}
