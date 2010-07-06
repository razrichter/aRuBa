package org.jcvi.annotation.facts;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

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
	public int hashCode() {
		return this.getId().hashCode();
	}
	
	public String toStringDetailReport() {
		String report = ">" + this.toStringReport() + "\n";

		// List the properties that are <requiredBy/SufficientFor/etc> this genome property
		DecimalFormat decimal = new DecimalFormat("0.000");
		HashMap<RelationshipType, List<Property>> relationships = this.getRelationships();
		for (RelationshipType type : relationships.keySet()) {
			for (Property property : relationships.get(type)) {
				report += property.getClass().getSimpleName() + "_" + property.getId() + "\t" + type.toString() + "\t" + "GenomeProperty_" + this.getId() + "\t" + decimal.format(property.getValue()) + "\n";
				//report += type.toString() + "\t" + property.toStringReport() + "\n";
			}
		}
		return report;
	}
	
	public String toStringReport() {
		DecimalFormat decimal = new DecimalFormat("0.000");
		return this.getClass().getSimpleName() + "_" + getId() + "\t" + getThreshold() + "\t" + getFilled() + "/" + getRequired() + "\t" + decimal.format(getValue()) + "\t" + getState().toString();
	}

	public String toString() {
		return this.getClass().getName() + "_" + getId();
	}
}

