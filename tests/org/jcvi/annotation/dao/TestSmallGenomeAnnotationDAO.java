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
		assertEquals(f.getFeatureId(), "GBAA_pXO2_0003");
	}
	public void testGetFeatureFalseCase() {
		Feature f = featureDAO.getFeature("no_such_feature");
		assertTrue(f == null);
	}
	public void testGetFeatureIdFalseCase() {
		Feature f = featureDAO.getFeature("GBAA_pXO2_0003");
		assertFalse(f.getFeatureId().equals("abc"));
	}
	
	public void testGetFeatures() {
		assertTrue(featureDAO.iterator().next() instanceof Feature);
	}
	
	public void testGetAnnotations() {
		assertTrue(annotationDAO.iterator().next() instanceof Annotation);
	}
	
	public void testGetFeatureAnnotations() {
		assertTrue(annotationDAO.iterator("GBAA_pXO2_0015").next() instanceof Annotation);
	}
	
	public void testGetAnnotationRoleIds() {
		Feature f = featureDAO.getFeature("GBAA_pXO2_0015");
		List<String> roleIds = annotationDAO.getRoleIds(f.getFeatureId());
		assertTrue(roleIds.contains("156"));
	}
	
	public void testGetAnnotationGoIds() {
		Feature f = new Feature("GBAA_pXO2_0015");
		List<String> goIds = annotationDAO.getGoIds(f.getFeatureId());
		assertTrue(goIds.contains("GO:0008150"));
		
		// Add the GO Ids to the annotation
		Annotation annot = new Annotation();
		annot.addGoIds(goIds);
		
		// Assert annotation on the feature
		f.setAssignedAnnotation(annot);
		
		// Test that there is an asserted annotation
		// with a GO assignment matching what we expect
		assertTrue(f.getGoIds().contains("GO:0008150"));
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
