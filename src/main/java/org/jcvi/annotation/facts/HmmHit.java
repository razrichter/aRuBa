package org.jcvi.annotation.facts;

import java.util.HashMap;

public class HmmHit {
	public static final int WEAK = 1;
	public static final int STRONG = 2;

	// evidence e, 
	// e.feat_name -> name
	// e.accession -> accession
	// e.id => hitId
	
	private Feature query;
	private String hitId;
	private String name; 
	private String accession;
	private double totalScore;
	private double domainScore;
	private int queryStart;
	private int queryEnd;
	private int queryStrand;
	
	/*
	private int hitStrength;
	private String algorithm;
	private String algorithmVersion;
	private HashMap<String, String> parameters; 
	private HashMap<String, String> statistics;
	*/

	public HmmHit(String hitId) {
		super();
		this.hitId = hitId;
	}

	public HmmHit(Feature query, String hitId) {
		this(hitId);
		this.query = query;
		this.hitId = hitId;
	}

	public String getHitId() {
		return hitId;
	}
	public void setHitId(String hitId) {
		this.hitId = hitId;
	}
	public Feature getQuery() {
		return query;
	}
	public void setQuery(Feature query) {
		this.query = query;
	}

	
}
