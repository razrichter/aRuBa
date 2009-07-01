package org.jcvi.annotation.facts;

public class Genome {
	private String id;
	private String organismName;

	public Genome(String id) {
		super();
		this.id = id;
	}
	public Genome(String id, String organismName) {
		super();
		this.id = id;
		this.organismName = organismName;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrganismName() {
		return organismName;
	}
	public void setOrganismName(String organismName) {
		this.organismName = organismName;
	}

}
