package org.jcvi.annotation.facts;
import java.util.Collection;
import java.util.HashMap;

/*
 * This is a flyweight class for caching equivalent GenomeProperty objects
 */

public class GenomeProperty extends Property {

	protected GenomeProperty(String id) {
		super(id);
	}
	public static GenomeProperty create(String id) {
		return GenomePropertyFactory.create(id);
	}

	public boolean equals(Object p) {
		if (p.getClass() == this.getClass()) {
			GenomeProperty fp = (GenomeProperty) p;
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

