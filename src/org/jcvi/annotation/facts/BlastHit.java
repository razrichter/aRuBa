package org.jcvi.annotation.facts;

public class BlastHit extends HomologyHit {
    private double eValue;
    private double bitScore;
    private double pValue;
    private double percentSimilarity;
    private double percentIdentity;

    public BlastHit() {
    	super();
    }
    public BlastHit(String queryId, String hitId) {
        super(queryId, hitId);
    }
    public BlastHit(String queryId, String hitId,
    		int queryStart, int queryEnd, int queryStrand) {
    	super(queryId, hitId, queryStart, queryEnd, queryStrand);
    }
    public BlastHit(String program, String queryId, String hitId,
            double eValue, double score, double bitScore, double pValue,
            int queryStart, int queryEnd, int queryStrand, int hitLength,
            int hitStart, int hitEnd, int hitStrand, double percentSimilarity,
            double percentIdentity) {
        super();
        this.program = program;
        this.queryId = queryId;
        this.hitId = hitId;
        this.eValue = eValue;
        this.score = score;
        this.bitScore = bitScore;
        this.pValue = pValue;
        this.queryStart = queryStart;
        this.queryEnd = queryEnd;
        this.queryStrand = queryStrand;
        this.hitLength = hitLength;
        this.hitStart = hitStart;
        this.hitEnd = hitEnd;
        this.hitStrand = hitStrand;
        this.percentSimilarity = percentSimilarity;
        this.percentIdentity = percentIdentity;
    }

    public double getEValue() {
        return eValue;
    }
    public void setEValue(double eValue) {
        this.eValue = eValue;
    }
    public double getpValue() {
        return pValue;
    }
    public void setpValue(double pValue) {
        this.pValue = pValue;
    }
    public double getBitScore() {
        return bitScore;
    }
    public void setBitScore(double bitScore) {
        this.bitScore = bitScore;
    }
    public double getPercentSimilarity() {
        return percentSimilarity;
    }
    public void setPercentSimilarity(double percentSimilarity) {
        this.percentSimilarity = percentSimilarity;
    }
    public double getPercentIdentity() {
        return percentIdentity;
    }
    public void setPercentIdentity(double percentIdentity) {
        this.percentIdentity = percentIdentity;
    }
}
