package org.jcvi.annotation.dao.tests;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import java.util.Iterator;

import org.jcvi.annotation.dao.*;
import org.jcvi.annotation.dao.factory.*;
import org.jcvi.annotation.facts.Feature;

public class TestSmallGenomeDAO extends TestCase {

	private SmallGenomeDAOFactory sgDAOFactory;
	private FeatureDAO featureDAO;
	
	@Before
	public void setUp() throws Exception {
		
		// get our Small Genome DAO Factory
		// sgDAOFactory = (SmallGenomeDAOFactory) DAOFactory.getDAOFactory(DAOFactory.SMALLGENOME);
		sgDAOFactory = new SmallGenomeDAOFactory();
		
		// Get our FeatureDAO and connect to gb6 database
		featureDAO = sgDAOFactory.getFeatureDAO("gb6");
		
		// Get our evidence DAOs (hmmSmallGenomeDAO, blastSmallGenomeDAO)
		// hmmDAO = smallGenomeDAOFactory.getHmmDAO();

	}
	
	public void testGetFeature() {
		Feature f = featureDAO.getFeature("GBAA_pXO2_0003");
		assertEquals(f.getFeatureId(), "GBAA_pXO2_0003");
	}
	public void testGetFeatureFalseCase() {
		Feature f = featureDAO.getFeature("no_such_feature");
		assertTrue(f == null);
	}
	public void testGetFeatureIdFalseCase() {
		Feature f = featureDAO.getFeature("GBAA_pXO2_0003");
		assertFalse(f.getFeatureId() == "abc");
	}
	
	public void testGetFeatures() {
		Iterator<Feature> features = featureDAO.getFeatures();
		assertTrue(features.hasNext());
		
		while (features.hasNext()) {
			System.out.println(features.next());
		}
	}
	
	
	@After
	public void tearDown() throws Exception {
	}

}
