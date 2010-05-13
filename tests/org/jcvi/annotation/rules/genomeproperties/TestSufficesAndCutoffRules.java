package org.jcvi.annotation.rules.genomeproperties;

import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.dao.RdfFactDAO;
import org.jcvi.annotation.dao.factory.SmallGenomeDAOFactory;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.Property;
import org.jcvi.annotation.rulesengine.RulesEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSufficesAndCutoffRules extends TestCase {

	private RdfFactDAO dao;
	private RulesEngine engine = new RulesEngine();
	
    @Before
    public void setUp() throws Exception {
		// URL rdfUrl = this.getClass().getResource("data/GenomeProperty_66644.rdf");
		URL n3Url = this.getClass().getResource("data/GenomeProperty_66644.n3");
		dao = new RdfFactDAO(n3Url, "N3");
		engine.addFacts(dao);
		
		addSmallGenome("gb6");

		// Add our Genome Property rules
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);
		engine.addResource(this.getClass().getResource("AboveTrustedCutoff.drl"), ResourceType.DRL);
    }
	
	@Test
	public void testRdfConverter() {
		assertTrue(dao instanceof org.jcvi.annotation.dao.RdfFactDAO);
		assertEquals(1, dao.getNumGenomeProperties());
		assertEquals(4, dao.getNumFeatureProperties());
		assertEquals(4, dao.getRelationships().size());
		assertEquals(9, dao.getTotalFacts());
		
	}

	@Test
	public void testSufficientPredicate() {
		
		
		// Fire our engine
		engine.fireAllRules();
		
	}

	public int addSmallGenome(String dbName) {

		if (dbName == null) {
			return 0;
		}
		
		System.out.println("Loading facts from Small Genome database " + dbName + "...");
		SmallGenomeDAOFactory sgFactory = new SmallGenomeDAOFactory(dbName);

		int total = 0;

		// Add Genome Features
		System.out.println("Adding features...");
		int count = total = engine.addFacts(sgFactory.getFeatureDAO());
		System.out.println("  " + count + " features");

		// Add annotations
		total += count = engine.addFacts(sgFactory.getAnnotationDAO());
		System.out.println("  " + count + " annotations");

		// Add Genome HMMs
		total += count = engine.addFacts(sgFactory.getHmmHitDAO());
		System.out.println("  " + count + " HMM hits");

		return total;
	}
	@After
	public void tearDown() throws Exception {
		//engine = null;
	}
}
