package org.jcvi.annotation.facts;
import java.util.List;

public class GoTerm {
	
	private String goId;
	private List<String> altGoIds;
	private String name;
	private String namespace;
	
	// Small Genome database specific stuff
	private String accessionCode;
	private String otherEvidence;

	
	public GoTerm(String goId) {
		super();
		this.goId = goId;
	}

	public String getGoId() {
		return goId;
	}
	public void setGoId(String goId) {
		this.goId = goId;
	}

	public List<String> getAltGoIds() {
		return altGoIds;
	}
	public void setAltGoIds(List<String> altGoIds) {
		this.altGoIds = altGoIds;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getAccessionCode() {
		return accessionCode;
	}
	public void setAccessionCode(String accessionCode) {
		this.accessionCode = accessionCode;
	}

	public String getOtherEvidence() {
		return otherEvidence;
	}
	public void setOtherEvidence(String otherEvidence) {
		this.otherEvidence = otherEvidence;
	}
	
	

}
