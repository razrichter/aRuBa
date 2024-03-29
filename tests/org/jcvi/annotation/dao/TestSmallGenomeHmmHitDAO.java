package org.jcvi.annotation.dao;
import junit.framework.TestCase;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.HmmHit;
import org.junit.After;
import org.junit.Before;

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
		assertTrue(hitDAO.getHmmHit("no_hit") == null);
	}

	public void testGetHits() {
		assertTrue(hitDAO.iterator().next() instanceof HmmHit);
	}

	@After
	public void tearDown() throws Exception {
	}

}
