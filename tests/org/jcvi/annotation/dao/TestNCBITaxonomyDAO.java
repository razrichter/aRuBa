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

		// Test the various ways of defining a new Taxon object
		Taxon t1 = new Taxon(new Integer(390805));
		Taxon t2 = taxonomyDAO.getTaxon(390805);
		Taxon t3 = taxonomyDAO.getTaxon("Geosporobacter");
		
		// Test the parents (should include Firmicutes and Clostridia)
		taxonomyDAO.getParents(t1);
		assertTrue(t1.getParentIds().contains(186801)); 
		// assertTrue(t1.getParentNames().contains("Clostridia"));

		taxonomyDAO.getParents(t2);
		assertTrue(t2.getParentIds().contains(1239)); 
		// assertTrue(t3.getParentNames().contains("Firmicutes"));

		taxonomyDAO.getParents(t3);
		assertTrue(t3.getParentIds().contains(1239)); 

	}
}