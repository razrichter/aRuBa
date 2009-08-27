package org.jcvi.annotation.dao;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.GenomeProperty;
import org.junit.After;
import org.junit.Before;

public class TestSmallGenomePropertyDAO extends TestCase {

	private SmallGenomeDAOFactory sgDAOFactory;
	private SmallGenomePropertyDAO genomePropertyDAO;
	
	@Before
	public void setUp() throws Exception {
		
		// We want to connect to common, but we use a different 
		// database name in our query to fetch the properties
		
		// get our Small Genome DAO Factory
		// sgDAOFactory = (SmallGenomeDAOFactory) DAOFactory.getDAOFactory(DAOFactory.SMALLGENOME);
		sgDAOFactory = new SmallGenomeDAOFactory("common");
		
		// Get our Feature DAO (connect to gb6 database)
		genomePropertyDAO = sgDAOFactory.getGenomePropertyDAO("gb6");
				
	}
	
	public void testgetProperty() {
		Map<String, Object> property = genomePropertyDAO.getProperty("GenProp0866");
		assertEquals("carbon metabolism", property.get("definition"));
	}
	public void testGetAllProperties() {
		
		// Test that number of properties is roughly what we expect
		List<GenomeProperty> properties = genomePropertyDAO.getProperties();
		assertTrue(properties.size() > 600);
		
		// Get some random genome property, and test its contained in the list
		Map<String, Object> property = genomePropertyDAO.getProperty("GenProp0812");
		assertEquals("rho-dependent termination", property.get("definition"));
		assertTrue(properties.contains(property));
	}
	public void testgetPropertyValue() {
		Map<String, Object> property = genomePropertyDAO.getProperty("GenProp0798");
		assertTrue(property.get("value").equals(0.375));
	}
	
	public void testGetAllPropertiesIterator() {
		for (Map<String, Object> prop : genomePropertyDAO) {
			System.out.println(prop.getClass().getName());
			assertTrue(prop.containsKey("id"));
			assertTrue(prop.containsKey("value"));
		}
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
