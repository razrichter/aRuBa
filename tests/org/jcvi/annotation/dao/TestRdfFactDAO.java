package org.jcvi.annotation.dao;

import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.jcvi.annotation.rulesengine.RulesEngine;
import org.junit.Before;
import org.junit.Test;

public class TestRdfFactDAO extends TestCase {

	private RdfFactDAO dao;
	private RulesEngine engine = new RulesEngine();
	
	
	@Test
	public void testRdfConverter() {
		URL rdfUrl = this.getClass().getResource("data/genomeproperties.rdf");
		dao = new RdfFactDAO(rdfUrl);
		
		// dao: an iterable of facts
		engine.addFacts(dao);
		
		assertTrue(dao instanceof org.jcvi.annotation.dao.RdfFactDAO);
		assertEquals(12931, dao.getTotalFacts());
		assertEquals(902, dao.getNumGenomeProperties());
		assertEquals(6078, dao.getNumFeatureProperties());
		assertEquals(5951, dao.getRelationships().size());
		assertEquals(12931, dao.getTotalFacts());
	}

}
