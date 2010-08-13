package org.jcvi.annotation.dao;

import java.net.URL;
import junit.framework.TestCase;
import org.junit.Test;

public class TestRdfFactDAO extends TestCase {

	@Test
	public void testRdfConverter() {
		String url = "data/genomeproperties.rdf";
		URL rdfUrl = this.getClass().getResource(url);
		RdfFactDAO dao = new RdfFactDAO(rdfUrl);
		
		assertEquals(908, dao.getNumGenomeProperties());
		assertEquals(5624, dao.getNumFeatureProperties());
		assertEquals(6005, dao.getRelationships().size());
		assertEquals(12537, dao.getTotalFacts());
	}
	
	@Test
	public void testN3Converter() {
		String url = "data/genomeproperties.n3";
		URL rdfUrl = this.getClass().getResource(url);
		RdfFactDAO dao = new RdfFactDAO(rdfUrl, "N3");
		
		assertEquals(520, dao.getNumGenomeProperties());
		assertEquals(5703, dao.getNumFeatureProperties());
		assertEquals(6188, dao.getRelationships().size());
		assertEquals(12411, dao.getTotalFacts());
	}

}
