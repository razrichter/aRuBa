package org.jcvi.annotation.dao;
import java.net.URL;
import junit.framework.TestCase;
import org.jcvi.annotation.facts.Taxon;
import org.junit.Before;

public class TestNCBITaxonomyDAO extends TestCase {
	private NCBITaxonomyDAO taxonomyDAO;
	
	@Before
	public void setUp() throws Exception {
		// Setup our taxonomy DAO
		// Downloaded from ftp://ftp.ncbi.nih.gov/pub/taxonomy/
		URL namesUrl = this.getClass().getResource("names.dmp");
		URL nodesFile = this.getClass().getResource("nodes.dmp");
		taxonomyDAO = new NCBITaxonomyDAO(namesUrl, nodesFile);
	}
	
	public void testGetTaxon() {
		Taxon taxon = taxonomyDAO.getTaxon("Geosporobacter");
		assertEquals(taxon.getTaxonId(), 390805);
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
}