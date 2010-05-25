package org.jcvi.annotation.facts;

import java.io.PrintStream;

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
	
	public PropertyState getState() {
		Double value = this.getValue();
		if (value != null) {
			if ( value == 0.0 ) {
				return PropertyState.NONE_FOUND;
			}
			else if ( value == 1.0 ) 
			{
				return PropertyState.YES;
			}
			else if ( value > 0.0 && value < this.getThreshold() ) 
			{
				return PropertyState.YES;
			}
			else if ( value > 0.0 && value > this.getThreshold() && value < 1.0 ) 
			{
				return PropertyState.SOME_EVIDENCE;
			}
		}
		return PropertyState.NONE_FOUND;
	}

	public static void report(PrintStream stream) {
		stream.println("Genome Properties Report");
		for (GenomeProperty p : GenomePropertyFactory.getProperties()) {
			stream.println(p.toStringReport());
		}
	}
	public int hashCode() {
		return this.getId().hashCode();
	}
	
	public String toStringReport() {
		return this.getClass().getSimpleName() + "\t" + getId() + "\t" + getValue() + "\t" + getFilled() + "/" + getRequired() + "\t" + getState().toString();
	}

	public String toString() {
		return this.getClass().getName() + "_" + getId();
	}
}

