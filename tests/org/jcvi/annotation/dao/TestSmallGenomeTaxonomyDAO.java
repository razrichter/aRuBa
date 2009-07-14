package org.jcvi.annotation.dao;

import junit.framework.TestCase;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.Taxon;
import org.junit.After;
import org.junit.Before;

public class TestSmallGenomeTaxonomyDAO extends TestCase {

	private SmallGenomeDAOFactory sgDAOFactory;
	private SmallGenomeTaxonomyDAO taxonomyDAO;
	
	@Before
	public void setUp() throws Exception {
		// Taxonomy is in SYBPANDA:promotheus database
		sgDAOFactory = new SmallGenomeDAOFactory("prometheus");
		taxonomyDAO = sgDAOFactory.getTaxonomyDAO();
	}
	
	public void testGetTaxon() {
		// Test true positives
		Taxon t = taxonomyDAO.getTaxon("Geosporobacter");
		assertEquals(t.getTaxonId(), 390805);
		t = taxonomyDAO.getTaxon(new Integer(390805));
		assertTrue(t.getName().equals("Geosporobacter"));
		
		// Test false positives
		try {
			t = taxonomyDAO.getTaxon("AlienCrittersFromMars");
			assertFalse(t instanceof Taxon);
		} catch (DaoException e) {
			assertTrue( e instanceof DaoException);
		}

		try {
			t = taxonomyDAO.getTaxon(new Integer(9999999));
			assertFalse(t instanceof Taxon);
		} catch (DaoException e) {
			assertTrue( e instanceof DaoException);
		}
	}
	public void testGetParents() {

		// Test getting the full taxonomy
		assertTrue(taxonomyDAO.iterator().next() instanceof Taxon);

		// Test the various ways of defining a new Taxon object
		Taxon t1 = new Taxon(new Integer(9999999));
		Taxon t2 = taxonomyDAO.getTaxon(390805);
		Taxon t3 = taxonomyDAO.getTaxon("Geosporobacter");
		
		// Test the parents (should include Firmicutes and Clostridia)
		assertFalse(taxonomyDAO.hasTaxon(t1));
		
		taxonomyDAO.getParents(t2);
		assertTrue(t2.getParentIds().contains(1239)); 
		assertTrue(t2.getParentNames().contains("Firmicutes"));

		taxonomyDAO.getParents(t3);
		assertTrue(t3.getParentIds().contains(1239));
		assertTrue(t3.getParentNames().contains("Firmicutes"));
		
	}

	@After
	public void tearDown() throws Exception {
	}

}
