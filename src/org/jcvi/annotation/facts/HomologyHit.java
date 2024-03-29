package org.jcvi.annotation.facts;

import java.text.DecimalFormat;

public class HomologyHit {
    protected String program;
    protected String queryId;
    protected String hitId;
    protected int queryStart;
    protected int queryEnd;
    protected int queryStrand;
    protected int queryLength;
    protected int hitStart;
    protected int hitEnd;
    protected int hitStrand;
    protected int hitLength;
    protected int alignmentLength;
    protected double score;
    protected HitStrengthType hitStrength;
    protected boolean assigned = false;

    public enum HitStrengthType {
        BELOW_NOISE, ABOVE_NOISE, ABOVE_TRUSTED
    }

    public HomologyHit() {
        super();
    }

    public HomologyHit(String queryId, String hitId) {
        super();
        this.queryId = queryId;
        this.hitId = hitId;
    }

    public HomologyHit(String queryId, String hitId, int queryStart,
            int queryEnd, int queryStrand) {
        this(queryId, hitId);
        this.queryStart = queryStart;
        this.queryEnd = queryEnd;
        this.queryStrand = queryStrand;
    }

    
    public boolean isAssigned() {
		return assigned;
	}

    public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}

	public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getHitId() {
        return hitId;
    }

    public void setHitId(String hitId) {
        this.hitId = hitId;
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

    public int getQueryLength() {
        return queryLength;
    }

    public void setQueryLength(int queryLength) {
        this.queryLength = queryLength;
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

    public void setHitLength(int hitLength) {
        this.hitLength = hitLength;
    }

    public int getHitLength() {
        return hitLength;
    }

    public int getAlignmentLength() {
        return alignmentLength;
    }

    public void setAlignmentLength(int alignmentLength) {
        this.alignmentLength = alignmentLength;
    }

    public double getHitPercentLength() {
        return ((double) 100 * (hitEnd - hitStart))/(double) hitLength;
    }

    public double getQueryPercentLength() {
        return ((double) 100 * (queryEnd - queryStart))/(double) queryLength;
    }

    public double getHitQueryLengthRatio() {
        // Need to cast to doubles, otherwise int/int returns an int
        double hitLength = Math.abs(hitEnd - hitStart);
        double queryLength = Math.abs(queryEnd - queryStart);
        double ratio = 100 * Math.abs((hitLength / queryLength) - 1);
        // round to two decimal places
        return Double.valueOf(new DecimalFormat("#.##").format(ratio));
    }

    public HitStrengthType getHitStrength() {
        return hitStrength;
    }
    
    public boolean isAboveTrustedHit() {
        return hitStrength!=null && hitStrength.equals(HitStrengthType.ABOVE_TRUSTED);
    }
    
    public boolean isAboveNoiseHit() {
        return hitStrength!=null &&  hitStrength.equals(HitStrengthType.ABOVE_NOISE);
    }
    
    public void setHitStrength(HitStrengthType hitStrength) {
        this.hitStrength = hitStrength;
    }
    
    public void setStrongHit() {
        this.hitStrength = HitStrengthType.ABOVE_TRUSTED;
    }
    
    public void setWeakHit() {
        this.hitStrength = HitStrengthType.ABOVE_NOISE;
    }
    public void setNonHit() {
        this.hitStrength = HitStrengthType.BELOW_NOISE;
    }
    
    public String toLocationString() {
    	return this.getHitId() + "." + hitStart + ".." + hitEnd + "." + hitStrand+ "|" 
    		 + this.getQueryId() + "." + queryStart + ".." + queryEnd + "." + queryStrand;
    }
    public String toString() {
    	return this.getClass().getName() + "." 
    		+ this.getQueryId() + "." + this.getHitId() + "." + this.getHitStrength().toString() + "." + this.hashCode();
    }
}
