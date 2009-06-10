package org.jcvi.genemodel;

import org.jcvi.genemodel.FeatureType;

public class Feature {
	private String featureId;
	private SourceMolecule source;
	public String name;
	private int start;
	private int end;
	private int strand;
	private int type; // Feature type

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
