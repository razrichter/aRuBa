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
	private int specificity;
	private int assertionType;
	private String commonName;
	private String geneSymbol;
	private String ecNumber;
	private ArrayList<String> roleIds = new ArrayList<String>();
	private ArrayList<String> goIds = new ArrayList<String>();

	
	public Annotation() {
		super();
	}

	public Annotation(String source) {
		super();
		this.source = source;
	}

	public Annotation(String source, double confidence, int specificity) {
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

	public int getSpecificity() {
		return specificity;
	}

	public void setSpecificity(int specificity) {
		this.specificity = specificity;
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

	public String getEcNumber() {
		return ecNumber;
	}

	public void setEcNumber(String ecNumber) {
		this.ecNumber = ecNumber;
	}

	public ArrayList<String> getRoleIds() {
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

	public ArrayList<String> getGoIds() {
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

}
