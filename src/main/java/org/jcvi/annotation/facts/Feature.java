package org.jcvi.annotation.facts;

import java.util.ArrayList;
import org.jcvi.annotation.facts.Annotation;

public class Feature {
	private String featureId;
	private SourceMolecule source;
	private int start;
	private int end;
	private int strand;
	private String type; // Feature type
	private Annotation assignedAnnotation;
	private ArrayList<Annotation> assertedAnnotations = new ArrayList<Annotation>();

	public Feature(String featureId, String type) {
		super();
		this.featureId = featureId;
		this.type = type;
	}
	
	// Constructor without requiring a source molecule
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
			SourceMolecule source) {
		super();
		this.featureId = featureId;
		this.source = source;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.type = type;
	}

	public Annotation getAssignedAnnotation() {
		return assignedAnnotation;
	}

	public void setAssignedAnnotation(Annotation assignedAnnotation) {
		this.assignedAnnotation = assignedAnnotation;
	}

	public ArrayList<Annotation> getAssertedAnnotations() {
		return assertedAnnotations;
	}

	public void addAssertedAnnotation(Annotation annotation) {
		this.assertedAnnotations.add(annotation);
	}

	public void removeAssertedAnnotation(Annotation annotation) {
		this.assertedAnnotations.remove(annotation);
	}
	
	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public SourceMolecule getSource() {
		return source;
	}

	public void setSource(SourceMolecule source) {
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
