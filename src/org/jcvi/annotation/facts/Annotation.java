package org.jcvi.annotation.facts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Annotation {
	
	// assertionType constants
	public static final int EXACT = 1;
	public static final int PARTIAL = 0;
	
	// specificity constants
	public static final int EQUIVALOG = 100;
	public static final int INIT_EQUIV = 95;
	public static final int DOMAIN = 70;
	
	private String source;
	private double confidence;
	private SpecificityType specificity;
	private int assertionType;
	private String commonName;
	private String geneSymbol;
	private List<String> ecNumbers = new ArrayList<String>();
	private List<String> roleIds = new ArrayList<String>();
	private List<String> goIds = new ArrayList<String>();

	
	public Annotation() {
		super();
	}

	public Annotation(String source) {
		super();
		this.source = source;
	}

	public Annotation(String source, double confidence, SpecificityType specificity) {
		super();
		this.source = source;
		this.confidence = confidence;
		this.specificity = specificity;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public SpecificityType getSpecificity() {
		return specificity;
	}

	public void setSpecificity(SpecificityType specType) {
		this.specificity = specType;
	}
	public int getAssertionType() {
		return assertionType;
	}

	public void setAssertionType(int assertionType) {
		this.assertionType = assertionType;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}

	// ec numbers
	public List<String> getEcNumbers() {
		return ecNumbers;
	}
	public void addEcNumbers(List<String> ecNumbers) {
		for (String o : ecNumbers) {
			this.ecNumbers.add(o);
		}
	}
	public void addEcNumbers(String ecNumberString) {
		addEcNumbers(Arrays.asList(ecNumberString.split(",")));
	}
	public void addEcNumber(String ecNumber) {
		this.ecNumbers.add(ecNumber);		
	}
	public void setEcNumbers(List<String> ecNumbers) {
		this.ecNumbers = ecNumbers;
	}
	public void setEcNumbers(String ecNumberString) {
		if (ecNumberString != null)
			setEcNumbers(Arrays.asList(ecNumberString.split(",")));
	}
	
	// role ids
	public List<String> getRoleIds() {
		return roleIds;
	}
	public void addRoleIds(List<String> roleIds) {
		for (String o : roleIds) {
			this.roleIds.add(o);
		}
	}
	public void addRoleIds(String roleIdString) {
		addRoleIds(Arrays.asList(roleIdString.split(",")));
	}
	public void addRoleId(String roleId) {
		this.roleIds.add(roleId);		
	}
	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}
	public void setRoleIds(String roleIdString) {
		setRoleIds(Arrays.asList(roleIdString.split(",")));
	}

	// go ids
	public void setGoIds(List<String> goIds) {
		this.goIds = goIds;
	}
	public void setGoIds(String goIdsString) {
		setGoIds(Arrays.asList(goIdsString.split(",")));
	}
	public List<String> getGoIds() {
		return goIds;
	}

	public void addGoIds(List<String> goIds) {
		for (String o : goIds) {
			this.goIds.add(o);
		}
	}
	
	public void addGoIds(String goIdsString) {
		addGoIds(Arrays.asList(goIdsString.split(",")));
	}
	
	public void addGoId(String goId) {
		this.goIds.add(goId);
	}
	public String toString() {
		return this.getClass().getName() + "." + this.getGeneSymbol() + "." + this.getCommonName();
	}
}
