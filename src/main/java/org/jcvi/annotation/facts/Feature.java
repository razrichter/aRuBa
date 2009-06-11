package org.jcvi.annotation.facts;

import java.util.ArrayList;

import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.FeatureType;

public class Feature {
	private String featureId;
	private SourceMolecule source;
	private int start;
	private int end;
	private int strand;
	private int type; // Feature type
	private Annotation assignedAnnotation;
	private ArrayList<Annotation> assertedAnnotations = new ArrayList<Annotation>();

	public Feature(String featureId, int type) {
		super();
		this.featureId = featureId;
		this.type = type;
	}

	public Feature(String featureId, SourceMolecule source, int start, int end,
			int strand, int type) {
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
