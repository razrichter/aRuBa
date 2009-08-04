package org.jcvi.annotation.facts;

public class HmmHit extends HomologyHit {
	public static final int WEAK = 1;
	public static final int STRONG = 2;
	
	private String name; 		// evidence.feat_name
	private String accession;	// evidence.accession
	private double domainScore;
	private int hitStrength;

	// Constructors
    public HmmHit() {
    	super();
    }
    public HmmHit(String queryId, String hitId) {
        super(queryId, hitId);
    }
    public HmmHit(String queryId, String hitId,
    		int queryStart, int queryEnd, int queryStrand) {
    	super(queryId, hitId);
    	this.queryStart = queryStart;
    	this.queryEnd = queryEnd;
    	this.queryStrand = queryStrand;
    }
    public HmmHit(String queryId, int queryStart, int queryEnd, int queryStrand,
    				String hitId, int hitStart, int hitEnd, int hitStrand) {
    	super(queryId, hitId, queryStart, queryEnd, queryStrand);
    	this.hitStart = hitStart;
    	this.hitEnd = hitEnd;
    	this.hitStrand = hitStrand;
    }
    
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}
	public double getDomainScore() {
		return domainScore;
	}
	public void setDomainScore(double domainScore) {
		this.domainScore = domainScore;
	}
	public int getHitStrength() {
		return hitStrength;
	}
	public void setHitStrength(int hitStrength) {
		this.hitStrength = hitStrength;
	}
	
}
