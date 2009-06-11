package org.jcvi.annotation.facts;

public class HmmHit {
	public static final int WEAK = 1;
	public static final int STRONG = 2;

	private Feature query;
	private String hitId;
	private int hitStrength;

	public HmmHit(Feature query, String hitId, int hitStrength) {
		super();
		this.query = query;
		this.hitId = hitId;
		this.hitStrength = hitStrength;
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

	public int getHitStrength() {
		return hitStrength;
	}

	public void setHitStrength(int hitStrength) {
		this.hitStrength = hitStrength;
	}

}
