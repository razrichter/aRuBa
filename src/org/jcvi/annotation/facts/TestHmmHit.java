package org.jcvi.annotation.facts;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.annotation.facts.HomologyHit.HitStrengthType;

public class TestHmmHit extends Feature {
	
	private HitStrengthType hitStrength;
	private String queryId;
	private String hitId;
	
    public TestHmmHit(String queryId, String hitId) {
        super(queryId);
        this.queryId = queryId;
        this.hitId = hitId;
    }
    
    public enum HitStrengthType {
        BELOW_NOISE, ABOVE_NOISE, ABOVE_TRUSTED
    }
    
    public boolean isAboveTrustedHit() {
        return hitStrength!=null && hitStrength.equals(HitStrengthType.ABOVE_TRUSTED);
    }

	public void setHitStrength(HitStrengthType hitStrength) {
		this.hitStrength = hitStrength;
		
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
    
    
}
