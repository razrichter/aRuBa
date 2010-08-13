package org.jcvi.annotation.facts;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public abstract class Property {

	private String id;
	private double threshold;
	private double value = 0;
	private double filled = 0;
	private double required = 0;
	private String definition;
	private HashMap<String, Object> attributes = new HashMap<String, Object>();
	
	// child relationship
	private HashSet<Property> requirements = new HashSet<Property>();
	private HashSet<Property> parts = new HashSet<Property>();
	private HashSet<Property> sufficients = new HashSet<Property>();
	
	// parent relationships
	private HashSet<Property> requiredBy = new HashSet<Property>();
	private HashSet<Property> partOf = new HashSet<Property>();
	private HashSet<Property> sufficientFor = new HashSet<Property>();
	
	protected Property(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public void setThreshold(String threshold) {
		this.threshold = new Double(threshold);
	}

	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public void setValue(String value) {
		this.value = new Double(value);
	}

	public double getFilled() {
		return filled;
	}
	public void setFilled(double filled) {
		this.filled = filled;
	}
	public double getRequired() {
		return required;
	}
	public void setRequired(double required) {
		this.required = required;
	}

	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public HashMap<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(HashMap<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public HashSet<Property> getRequirements() {
		return requirements;
	}
	public void setRequirements(HashSet<Property> requiredBy) {
		this.requirements = requiredBy;
	}
	public boolean addRequirement(Property p) {
		return this.requirements.add(p);
	}

	public HashSet<Property> getParts() {
		return parts;
	}
	public void setParts(HashSet<Property> parts) {
		this.parts = parts;
	}
	public boolean addPart(Property p) {
		return this.parts.add(p);
	}
	
	public HashSet<Property> getSufficients() {
		return sufficients;
	}
	public void setSufficients(HashSet<Property> sufficesFor) {
		this.sufficients = sufficesFor;
	}
	public boolean addSufficient(Property p) {
		return this.sufficients.add(p);
	}

	
	public HashSet<Property> getRequiredBy() {
		return requiredBy;
	}
	public void setRequiredBy(HashSet<Property> requiredBy) {
		this.requiredBy = requiredBy;
	}
	public HashSet<Property> getPartOf() {
		return partOf;
	}
	public void setPartOf(HashSet<Property> partOf) {
		this.partOf = partOf;
	}
	public HashSet<Property> getSufficientFor() {
		return sufficientFor;
	}
	public void setSufficientFor(HashSet<Property> sufficientFor) {
		this.sufficientFor = sufficientFor;
	}
	public boolean addPartOf(Property p) {
		return this.partOf.add(p);
	}
	public boolean addSufficientFor(Property p) {
		return this.sufficientFor.add(p);
	}
	public boolean addRequiredBy(Property p) {
		return this.requiredBy.add(p);
	}
	
	public HashMap<RelationshipType, HashSet<Property>> getChildRelationships() {
		HashMap<RelationshipType, HashSet<Property>> relationships = new HashMap<RelationshipType, HashSet<Property>>();
		relationships.put(RelationshipType.PART_OF, this.getParts());
		relationships.put(RelationshipType.SUFFICIENT_FOR, this.getSufficients());
		relationships.put(RelationshipType.REQUIRED_BY, this.getRequirements());
		return relationships;
	}
	public HashMap<RelationshipType, HashSet<Property>> getParentRelationships() {
		HashMap<RelationshipType, HashSet<Property>> relationships = new HashMap<RelationshipType, HashSet<Property>>();
		relationships.put(RelationshipType.PART_OF, this.getPartOf());
		relationships.put(RelationshipType.SUFFICIENT_FOR, this.getSufficientFor());
		relationships.put(RelationshipType.REQUIRED_BY, this.getRequiredBy());
		return relationships;
	}
	
	public String toStringReport() {
		DecimalFormat decimal = new DecimalFormat("0.00");
		return this.getClass().getSimpleName() + "_" + getId() + "\t" + getThreshold() + "\t" + getFilled() + "/" + getRequired() + "\t" + decimal.format(getValue());
	}
	
	public boolean addParentRelationship(RelationshipType type, Property parent) {
		if (type.equals(RelationshipType.REQUIRED_BY)) {
			return this.addRequiredBy(parent);
		}
		else if (type.equals(RelationshipType.PART_OF))
		{	
			return this.addPartOf(parent);
		}
		else if (type.equals(RelationshipType.SUFFICIENT_FOR))
		{
			return this.addSufficientFor(parent);
		}
		return false;
	}	
	public boolean addChildRelationship(RelationshipType type, Property child) {
		if (type.equals(RelationshipType.REQUIRED_BY)) {
			return this.addRequirement(child);
		}
		else if (type.equals(RelationshipType.PART_OF))
		{
			return this.addPart(child);
		}
		else if (type.equals(RelationshipType.SUFFICIENT_FOR))
		{
			return this.addSufficient(child);
		}
		return false;
	}	
}
