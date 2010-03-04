package org.jcvi.annotation.facts;

public enum SpecificityType {
	INITIALIZE,
	JOC_MGAT_SUBFAM,
	FAMILY_COMPLETE,
	JOC_MGAT_EQUIV,
	EQUIV_BY_GENE_SYM,
	INIT_EQUIV,
	GO_ADDITIONS,
	EQUIVALOG,
	OVER_EQUIV;

	public int getRank() {
		if (this.equals(INITIALIZE)) {
			return 30;
		} else if (this.equals(JOC_MGAT_SUBFAM)) {
			return 45;
		} else if (this.equals(FAMILY_COMPLETE)) {
			return 50;
		} else if (this.equals(JOC_MGAT_EQUIV)) {
			return 60;
		} else if (this.equals(EQUIV_BY_GENE_SYM)) {
			return 68;
		} else if (this.equals(INIT_EQUIV)) {
			return 70;
		} else if (this.equals(GO_ADDITIONS)) {
			return 75;
		} else if (this.equals(EQUIVALOG)) {
			return 95;
		} else if (this.equals(OVER_EQUIV)) {
			return 120;
		} else {
			return 0;
		}
	}

}
