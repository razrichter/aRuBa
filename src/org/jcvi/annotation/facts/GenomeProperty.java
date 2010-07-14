package org.jcvi.annotation.facts;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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

	public double getRequired() {
		List<Property> requiredRelations = this.getRelationshipsByType(RelationshipType.REQUIRED_BY);
		return (requiredRelations == null) ? 0.0 : requiredRelations.size();
	}
	
	public double getFilled() {
		List<Property> requiredRelations = this.getRelationshipsByType(RelationshipType.REQUIRED_BY);
		double numFilled = 0.0;
		if (requiredRelations != null) {
			for (Property property : requiredRelations) {
				numFilled += property.getValue();
			}
		}
		return numFilled;
	}
	public double getValue() {
		double numRequired = this.getRequired();
		double numFilled = this.getFilled();
 		return (numRequired == 0) ? 0
				: numFilled/numRequired;
		
	}
	
	public PropertyState getState() {
		// Double value = this.getValue();
		Double numFilled = this.getFilled();
		Double threshold = this.getThreshold();
		Double numRequired = this.getRequired();
		
		if (numFilled != null) {
			if ( numFilled == 0.0 ) {
				return PropertyState.NONE_FOUND;
			}
			else if ( numFilled.equals(numRequired) ) 
			{
				return PropertyState.YES;
			}
			else if ( numFilled > 0.0 && numFilled < threshold ) 
			{
				return PropertyState.NOT_SUPPORTED;
			}
			else if ( numFilled >= threshold && numFilled < numRequired ) 
			{
				return PropertyState.SOME_EVIDENCE;
			}
		}
		return PropertyState.NONE_FOUND;
	}

	public static void report(PrintStream stream) {
		stream.println("Genome Properties Report");
		stream.println("# <GenomeProperty Id> <Threshold> <Filled>/<Total> <Fraction Filled> <State>");
		for (GenomeProperty p : GenomePropertyFactory.getProperties()) {
			stream.println(p.toStringReport());
		}
	}
	public static void detailReport(PrintStream stream) {
		stream.println("# Genome Properties Report");
		for (GenomeProperty p : GenomePropertyFactory.getProperties()) {
			stream.print(p.toStringDetailReport());
		}
	}
	
	public String toStringDetailReport() {
		String report = ">" + this.toStringReport() + "\n";

		// List the properties that are <requiredBy/SufficientFor/etc> this genome property
		DecimalFormat decimal = new DecimalFormat("0.000");
		HashMap<RelationshipType, List<Property>> relationships = this.getRelationships();

		for (Entry<RelationshipType, List<Property>> typeProperties : relationships.entrySet()) {
			for (Property property : typeProperties.getValue()) {
				report += property.getClass().getSimpleName() + "_" + property.getId() + "\t" + typeProperties.getKey().toString() + "\t" + "GenomeProperty_" + this.getId() + "\t" + decimal.format(property.getValue()) + "\n";
			}
		}
		return report;
	}
	
	public String toStringReport() {
		DecimalFormat decimal = new DecimalFormat("0.000");
		return this.getClass().getSimpleName() + "_" + getId() + "\t" + getThreshold() + "\t" + getFilled() + "/" + getRequired() + "\t" + decimal.format(getValue()) + "\t" + getState().toString();
	}

	public boolean equals(Object p) {
	    //use instanceof instead of getClass here for two reasons
	    //1. if need be, it can match any supertype, and not just one class;
	    //2. it renders an explict check for "that == null" redundant, since
	    //it does the check for null already - "null instanceof [type]" always
	    //returns false. (See Effective Java by Joshua Bloch.)
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
		return this.getClass().getName() + "_" + getId();
	}
}

