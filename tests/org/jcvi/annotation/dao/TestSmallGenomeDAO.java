package org.jcvi.annotation.dao;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import java.util.List;

import org.jcvi.annotation.dao.factory.*;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;

public class TestSmallGenomeDAO extends TestCase {

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
		assertTrue(featureDAO.iterator().next() instanceof Feature);
	}
	
	public void testGetAnnotations() {
		assertTrue(annotationDAO.iterator().next() instanceof Annotation);
	}
	
	public void testGetFeatureAnnotations() {
		Feature feat = featureDAO.getFeatureById(172227);
		assertTrue(annotationDAO.iterator(feat).next() instanceof Annotation);
	}

	public void testGetAnnotationRoleIdsByFeature() {
		Feature feat = featureDAO.getFeatureById(172227);
		for (Annotation annot : annotationDAO.getAnnotations(feat)) {
			List<String> roleIds = annot.getRoleIds();
			assertTrue(roleIds.size() > 0);
		}
	}
	
	public void testGetAnnotationRoleIds() {
		List<String> roleIds = annotationDAO.getRoleIds("GBAA_pXO1_0083");
		assertTrue(roleIds.contains("188"));
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
