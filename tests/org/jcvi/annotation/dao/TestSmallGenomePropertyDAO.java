package org.jcvi.annotation.dao;

import java.util.List;

import junit.framework.TestCase;

import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.Property;
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
		Property property = genomePropertyDAO.getProperty("GenProp0866");
		assertEquals("carbon metabolism", property.getDefinition());
	}
	public void testGetAllProperties() {
		
		// Test that number of properties is roughly what we expect
		List<GenomeProperty> properties = genomePropertyDAO.getProperties();
		assertTrue(properties.size() > 600);
		
		// Get some random genome property, and test its contained in the list
		Property property = genomePropertyDAO.getProperty("GenProp0812");
		assertEquals("rho-dependent termination", property.getDefinition());
		assertTrue(properties.contains(property));
	}
	public void testgetPropertyValue() {
		Property property = genomePropertyDAO.getProperty("GenProp0798");
		assertTrue(property.getValue() == 0.375);
	}
	
	public void testGetAllPropertiesIterator() {
		for (Property prop : genomePropertyDAO) {
			System.out.println(prop.getClass().getName());
			assertTrue(prop.getId() != null);
		}
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
