package org.jcvi.annotation.facts;

public class BlastpHit {
	private Feature query;
	private String hitId;
	private double eValue;
	private double score;
	private double bitScore;
	private double pValue;
	private int queryStart;
	private int queryEnd;
	private int queryStrand;
	private int hitLength;
	private int hitStart;
	private int hitEnd;
	private int hitStrand;
	private double percentSimilarity;
	private double percentIdentity;

	public BlastpHit(Feature query, String hitId) {
		super();
		this.query = query;
		this.hitId = hitId;
	}
	
	
	public BlastpHit(Feature query, String hitId, double eValue, double score,
			double bitScore, double pValue, int queryStart, int queryEnd,
			int queryStrand, int hitLength, int hitStart, int hitEnd,
			int hitStrand, double percentSimilarity, double percentIdentity) {
		super();
		this.query = query;
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


	public Feature getQuery() {
		return query;
	}

	public void setQuery(Feature query) {
		this.query = query;
	}

	public String getHitId() {
		return hitId;
	}

	public void setHitId(String hitId) {
		this.hitId = hitId;
	}

	public double getEValue() {
		return eValue;
	}

	public void setEValue(double value) {
		eValue = value;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getBitScore() {
		return bitScore;
	}

	public void setBitScore(double bitScore) {
		this.bitScore = bitScore;
	}

	public double getPValue() {
		return pValue;
	}

	public void setPValue(double value) {
		pValue = value;
	}

	public int getQueryStart() {
		return queryStart;
	}

	public void setQueryStart(int queryStart) {
		this.queryStart = queryStart;
	}

	public int getQueryEnd() {
		return queryEnd;
	}

	public void setQueryEnd(int queryEnd) {
		this.queryEnd = queryEnd;
	}

	public int getQueryStrand() {
		return queryStrand;
	}

	public void setQueryStrand(int queryStrand) {
		this.queryStrand = queryStrand;
	}

	public void setHitLength(int hitLength) {
		this.hitLength = hitLength;
	}

	public int getHitLength() {
		return hitLength;
	}
	
	public double getHitPercentLength() {
		return 100*(hitEnd-hitStart)/hitLength;
	}
	public double getQueryPercentLength() {
		return 100*(queryEnd-queryStart)/query.getLength();
	}
		

	public int getHitStart() {
		return hitStart;
	}

	public void setHitStart(int hitStart) {
		this.hitStart = hitStart;
	}

	public int getHitEnd() {
		return hitEnd;
	}

	public void setHitEnd(int hitEnd) {
		this.hitEnd = hitEnd;
	}

	public int getHitStrand() {
		return hitStrand;
	}

	public void setHitStrand(int hitStrand) {
		this.hitStrand = hitStrand;
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
	
	public double getHitQueryLengthRatio() {
		return 100*Math.abs((hitEnd - hitStart)/(queryEnd-queryStart) - 1);
	}

}
