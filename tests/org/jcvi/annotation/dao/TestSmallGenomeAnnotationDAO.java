package org.jcvi.annotation.dao;

import java.util.List;
import junit.framework.TestCase;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.junit.After;
import org.junit.Before;

public class TestSmallGenomeAnnotationDAO extends TestCase {

	private SmallGenomeDAOFactory sgDAOFactory;
	private SmallGenomeFeatureDAO featureDAO;
	private SmallGenomeAnnotationDAO annotationDAO;
	
	@Before
	public void setUp() throws Exception {
		
		// get our Small Genome DAO Factory
		// sgDAOFactory = (SmallGenomeDAOFactory) DAOFactory.getDAOFactory(DAOFactory.SMALLGENOME);
		sgDAOFactory = new SmallGenomeDAOFactory("gb6");
		
		// Get our Feature DAO (connect to gb6 database)
		featureDAO = sgDAOFactory.getFeatureDAO();
				
		// Get our Annotation DAO
		annotationDAO = sgDAOFactory.getAnnotationDAO("gb6");
		
		// Get our evidence DAOs (hmmSmallGenomeDAO, blastSmallGenomeDAO)
		// hmmDAO = smallGenomeDAOFactory.getHmmDAO();

	}
	
	public void testGetFeature() {
		Feature f = featureDAO.getFeature("GBAA_pXO2_0003");
		assertEquals(f.getName(), "GBAA_pXO2_0003");
	}
	public void testGetFeatureFalseCase() {
		Feature f = featureDAO.getFeature("no_such_feature");
		assertTrue(f == null);
	}
	public void testGetFeatureIdFalseCase() {
		Feature f = featureDAO.getFeature("GBAA_pXO2_0003");
		assertFalse(f.getName() == "abc");
	}
	
	public void testGetFeatures() {
		for (Feature f : featureDAO) {
			assertTrue(f instanceof Feature);
		}
	}
	
	public void testGetAnnotations() {
		assertTrue(annotationDAO.iterator().next() instanceof Annotation);
	}
	
	public void testGetFeatureAnnotations() {
		Feature feat = featureDAO.getFeatureById(172227);
		assertTrue(annotationDAO.iterator(feat).next() instanceof Annotation);
	}
	
	public void testGetAnnotationRoleIds() {
		Feature f = featureDAO.getFeatureById(172227);
		List<String> roleIds = annotationDAO.getRoleIds(f.getName());
		assertTrue(roleIds.contains("188"));
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
