package org.jcvi.annotation.dao;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import java.util.List;

import org.jcvi.annotation.dao.factory.*;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.HmmHit;

public class TestSmallGenomeHmmHitDAO extends TestCase {

	private SmallGenomeDAOFactory sgDAOFactory;
	private SmallGenomeHmmHitDAO hitDAO;
	
	@Before
	public void setUp() throws Exception {
		
		// get our Small Genome DAO Factory
		sgDAOFactory = new SmallGenomeDAOFactory("gb6");
		
		// Get our HmmHitDAO (connect to gb6 database)
		hitDAO = sgDAOFactory.getHmmHitDAO();
	}
	
	public void testGetHit() {
		HmmHit hit = hitDAO.getHmmHit("PF08279");
		assertEquals("GBAA_pXO2_0060", hit.getQueryId());
	}
	public void testGetHitFalseCase() {
		HmmHit hit = hitDAO.getHmmHit("no_hit");
		assertTrue(hit == null);
	}

	public void testGetHits() {
		for (HmmHit hit : hitDAO) {
			assertTrue(hit instanceof HmmHit);
			break;
		}
	}

	@After
	public void tearDown() throws Exception {
	}

}
