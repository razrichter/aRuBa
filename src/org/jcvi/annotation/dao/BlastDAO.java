package org.jcvi.annotation.dao;

import org.jcvi.annotation.facts.BlastHit;

public interface BlastDAO {
	// Reading methods	
	// BlastHit getHit(String BlastHitId);
	Iterable<BlastHit> getHits();
	
	// Writing (CRUD) methods
	// boolean addHit(BlastHit hit);
	// boolean deleteHit(BlastHit hit);
	// boolean updateHit(BlastHit hit);

}
