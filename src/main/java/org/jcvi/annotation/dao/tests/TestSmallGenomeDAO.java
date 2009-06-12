package org.jcvi.annotation.dao.tests;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import java.sql.Connection;
import org.jcvi.annotation.dao.*;
import org.jcvi.annotation.dao.factory.*;
import org.jcvi.annotation.facts.Feature;

public class TestSmallGenomeDAO extends TestCase {

	private DAOFactory smallGenomeDAOFactory;
	private FeatureDAO featureDAO;
	
	@Before
	public void setUp() throws Exception {
		
		// get our Small Genome DAO Factory
		smallGenomeDAOFactory = DAOFactory.getDAOFactory(DAOFactory.SMALLGENOME);
	
		// Get our FeatureDAO
		featureDAO = smallGenomeDAOFactory.getFeatureDAO();
		
		// hmmDAO = smallGenomeDAOFactory.getHmmDAO();

		SmallGenomeDAOFactory.createConnection();
		
		// Connection conn = SmallGenomeDAOFactory.
		// SmallGenomeDAOFactory.createConnection()
	
	}
	
	public void testGetFeature() {
		Feature f = featureDAO.getFeature("abc");
		assertEquals(f.getFeatureId(), "abc");
	}
	

	
	@After
	public void tearDown() throws Exception {
	}

}
