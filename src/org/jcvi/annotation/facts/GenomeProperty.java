package org.jcvi.annotation.facts;

import java.util.ArrayList;
import java.util.List;

/*
 * This is a flyweight class for caching equivalent GenomeProperty objects
 */

public class GenomeProperty extends Property {

	// parent and children are used to capture the DAG of Genome Properties
	private GenomeProperty parent;
	private List<GenomeProperty> children;

	protected GenomeProperty(String id) {
		super(id);
	}

	public static GenomeProperty create(String id) {
		return GenomePropertyFactory.create(id);
	}

	public GenomeProperty getParent() {
		return parent;
	}

	public void setParent(GenomeProperty parent) {
		this.parent = parent;
	}

	public List<GenomeProperty> getChildren() {
		return children;
	}

	public void setChildren(List<GenomeProperty> children) {
		this.children = children;
	}

	public void addChild(GenomeProperty child) {
		List<GenomeProperty> children = this.getChildren();
		if (children == null) {
			children = new ArrayList<GenomeProperty>();
			this.setChildren(children);
		}
		children.add(child);
	}

	public PropertyState getState() {
		// Double value = this.getValue();
		Double numFilled = this.getFilled();
		Double threshold = this.getThreshold();
		Double numRequired = this.getRequired();

		if (numFilled != null) {
			if (numFilled == 0.0) {
				return PropertyState.NONE_FOUND;
			} else if (numFilled.equals(numRequired)) {
				return PropertyState.YES;
			} else if (numFilled > 0.0 && numFilled < threshold) {
				return PropertyState.NOT_SUPPORTED;
			} else if (numFilled >= threshold && numFilled < numRequired) {
				return PropertyState.SOME_EVIDENCE;
			}
		}
		return PropertyState.NONE_FOUND;
	}

	public static GenomeProperty get(String id) {
		return GenomePropertyFactory.get(id);
	}
	public static boolean has(String id) {
		return GenomePropertyFactory.has(id);
	}

	public boolean equals(Object p) {
		// use instanceof instead of getClass here for two reasons
		// 1. if need be, it can match any supertype, and not just one class;
		// 2. it renders an explict check for "that == null" redundant, since
		// it does the check for null already - "null instanceof [type]" always
		// returns false. (See Effective Java by Joshua Bloch.)
		if (p instanceof GenomeProperty) {
			GenomeProperty gp = (GenomeProperty) p;
			if (gp.getId().equals(this.getId())) {
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
	}
}
