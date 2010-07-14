package org.jcvi.annotation.facts;

import java.util.ArrayList;
import java.util.List;

public class Feature {
	private String featureId;
	private Genome genome = new Genome();
	private Feature source;
	private String name;
	private int start;
	private int end;
	private int strand;
	private boolean isCircular = false;
	private String type; // Feature type
	private List<Property> properties = new ArrayList<Property>();
	private Annotation assignedAnnotation;
	private List<Annotation> assertedAnnotations = new ArrayList<Annotation>();
		
	public Feature(String featureId) {
		super();
		this.featureId = featureId;
	}
	public Feature(String featureId, String type) {
		this(featureId);
		this.type = type;
	}
	
	// Constructor with coordinates
	public Feature(String featureId, String type, int start, int end, int strand) {
		super();
		this.featureId = featureId;
		this.type = type;
		this.start = start;
		this.end = end;
		this.strand = strand;
	}

	// Constructor with source molecule
	public Feature(String featureId, String type, int start, int end, int strand,
			Feature source) {
		this(featureId, type, start, end, strand);
		this.setSource(source);
	}

	// Constructor with name
	public Feature(String featureId, String type, int start, int end, int strand,
			String name) {
		this(featureId, type, start, end, strand);
		this.setName(name);
	}
	
	public Genome getGenome() {
		return genome;
	}
	public void setGenome(Genome genome) {
		this.genome = genome;
	}
	public Taxon getTaxon() {
		return this.genome.getTaxon();
	}
	
	public List<Property> getProperties() {
		return this.properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public void addProperty(Property prop) {
		this.properties.add(prop);
	}
	public Annotation getAssignedAnnotation() {
		return assignedAnnotation;
	}
	public void setAssignedAnnotation(Annotation assignedAnnotation) {
		this.assignedAnnotation = assignedAnnotation;
	}

	public List<Annotation> getAssertedAnnotations() {
		return assertedAnnotations;
	}

	public void addAssertedAnnotation(Annotation annotation) {
		this.assertedAnnotations.add(annotation);
	}

	public void removeAssertedAnnotation(Annotation annotation) {
		this.assertedAnnotations.remove(annotation);
	}
	
	public List<String> getGoIds() {
		return this.getAssignedAnnotation().getGoIds();
	}
	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Feature getSource() {
		return source;
	}

	public void setSource(Feature source) {
		this.source = source;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getStrand() {
		return strand;
	}

	public int getLength() {
		return end - start;
	}
	public void setStrand(int strand) {
		this.strand = strand;
	}
	public boolean isCircular() {
		return isCircular;
	}
	public void setCircular(boolean isCircular) {
		this.isCircular = isCircular;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean hasOverlap(Feature f) {
		if (this.source == f.getSource()) {
			if ((f.getEnd() > this.start && f.getStart() < this.end) 
					|| (f.getEnd() > this.start && f.getStart() < this.end)) {
				return true;
			}
		}
		return false;
	}
	public int getDistance(String sourceId, int start, int end, boolean isCircular) {
		if (this.source.getFeatureId().equals(sourceId)) {
			// Check for overlaps
			if ((end > this.start && start < this.end) || (end > this.start && start < this.end)) {
				return 0;
			} else if (end < this.start) {
				return this.start - end;
			} else if (start > this.end) {
				return start - this.end;
			}
		}
		return -1;		
	}
	public int getDistance(Feature f) {
		return getDistance(f.getSource().getFeatureId(), f.getStart(), f.getEnd(), f.getSource().isCircular());
	}
	public boolean isWithin(Feature f, int maxDist) {
		if (f == null 
				|| this.getSource() == null
				|| f.getSource() == null 
				|| !(f.getSource().getFeatureId().equals(this.getSource().getFeatureId()))) {
			return false;
		}
		int dist = this.getDistance(f);
		return (dist != -1 && dist < maxDist);
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + "." + this.featureId;
	}
	
	public boolean isSameDirection(Feature f1, Feature f2) {
		return (f1.getStrand() == f2.getStrand());
	}

	public void removeProperty(GenomeProperty p) {
		this.removeProperty(p.getId().toString());
	}
	public void removeProperty(FeatureProperty p) {
		this.removeProperty(p.getId().toString());
	}
	public void removeProperty(String id) {
		Property prop;
		for (int i=0; i < this.properties.size(); i++) {
			prop = this.properties.get(i);
			if (prop.getId().equals(id)) {
				this.properties.remove(i);
			}
		}
	}
	
	// @Override equals method
	public boolean equals(Object o) {
		if (o instanceof Feature) {
			Feature f = (Feature) o;
			if (f.toString().equals(this.toString())) {
				return true;
			}
		}
		return false;
	}
	
	// @Override hashCode method
	public int hashCode() {
		return this.toString().hashCode();
	}
}
