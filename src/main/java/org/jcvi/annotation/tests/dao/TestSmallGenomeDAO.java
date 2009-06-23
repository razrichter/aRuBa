package org.jcvi.annotation.tests.dao;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import java.util.Iterator;
import java.util.List;

import org.jcvi.annotation.dao.*;
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
		Iterator<Feature> features = featureDAO.getFeatures();
		assertTrue(features.hasNext());
	}
	
	public void testGetAnnotations() {
		Iterator<Annotation> annotations = annotationDAO.getAnnotations();
		assertTrue(annotations.hasNext());
	}
	
	public void testGetFeatureAnnotations() {
		Feature f = featureDAO.getFeatureById(172227);
		Iterator<Annotation> annotations = annotationDAO.getAnnotations(f);
		assertTrue(annotations.hasNext());
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
