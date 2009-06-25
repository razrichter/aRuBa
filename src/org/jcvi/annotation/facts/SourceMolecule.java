package org.jcvi.annotation.facts;

public class SourceMolecule {
	private Genome genome;
	private String moleculeId;

	public SourceMolecule(Genome genome, String moleculeId) {
		super();
		this.genome = genome;
		this.moleculeId = moleculeId;
	}

	public Genome getGenome() {
		return genome;
	}

	public void setGenome(Genome genome) {
		this.genome = genome;
	}

	public String getMoleculeId() {
		return moleculeId;
	}

	public void setMoleculeId(String moleculeId) {
		this.moleculeId = moleculeId;
	}

}
