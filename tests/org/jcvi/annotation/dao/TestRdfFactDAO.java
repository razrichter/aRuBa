package org.jcvi.annotation.dao;

import java.net.URL;
import junit.framework.TestCase;
import org.jcvi.annotation.rulesengine.RulesEngine;
import org.junit.Test;

public class TestRdfFactDAO extends TestCase {

	private RdfFactDAO dao;
	private RulesEngine engine = new RulesEngine();
	
	@Test
	public void testRdfConverter() {
		String url = "data/genomeproperties.n3";
		URL rdfUrl = this.getClass().getResource(url);
		dao = new RdfFactDAO(rdfUrl, "N3");
		
		// RdfFactDAO is an iterable of facts
		engine.addFacts(dao);
		
		assertEquals(908, dao.getNumGenomeProperties());
		assertEquals(5624, dao.getNumFeatureProperties());
		assertEquals(6005, dao.getRelationships().size());
		assertEquals(12537, dao.getTotalFacts());
	}
	
	@Test
	public void testN3Converter() {
		String url = "data/genomeproperties.n3";
		URL rdfUrl = this.getClass().getResource(url);
		dao = new RdfFactDAO(rdfUrl, "N3");
		
		// RdfFactDAO is an iterable of facts
		engine.addFacts(dao);
		
		assertEquals(908, dao.getNumGenomeProperties());
		assertEquals(5624, dao.getNumFeatureProperties());
		assertEquals(6005, dao.getRelationships().size());
		assertEquals(12537, dao.getTotalFacts());
	}

}
