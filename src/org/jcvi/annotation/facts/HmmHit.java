package org.jcvi.annotation.facts;

public class HmmHit extends HomologyHit {
	
	private String name; 		// evidence.feat_name
	private String accession;	// evidence.accession
	private double domainScore;

	// Constructors
    public HmmHit() {
    	super();
    }
    public HmmHit(String queryId, String hitId) {
        super(queryId, hitId);
        this.program = "hmm";
    }
    public HmmHit(String queryId, String hitId,
    		int queryStart, int queryEnd, int queryStrand) {
    	super(queryId, hitId);
    	this.program = "hmm";
    	this.queryStart = queryStart;
    	this.queryEnd = queryEnd;
    	this.queryStrand = queryStrand;
    }
    public HmmHit(String queryId, int queryStart, int queryEnd, int queryStrand,
    				String hitId, int hitStart, int hitEnd, int hitStrand) {
    	super(queryId, hitId, queryStart, queryEnd, queryStrand);
    	this.program = "hmm";
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
	
}
