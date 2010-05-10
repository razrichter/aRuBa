package org.jcvi.annotation.rules.genomeproperties;

import java.net.URL;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.dao.RdfFactDAO;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.rulesengine.RulesEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestHasSufficientButNotRequiredGenomeProperty extends TestCase {

	private RdfFactDAO dao;
	private RulesEngine engine = new RulesEngine();
	
    @Before
    public void setUp() throws Exception {
		URL n3Url = this.getClass().getResource("data/GenomePropertySufficientRequired.n3");
		dao = new RdfFactDAO(n3Url, "N3");
		engine.addFacts(dao);
		
    }
	
	@Test
	public void testRdfConverter() {
		assertTrue(dao instanceof org.jcvi.annotation.dao.RdfFactDAO);
		assertEquals(1, dao.getNumGenomeProperties());
		assertEquals(2, dao.getNumFeatureProperties());
		assertEquals(2, dao.getRelationships().size());
		assertEquals(5, dao.getTotalFacts());
	}

	@Test
	public void testHasSufficientButNotRequired() {
		
		// Add our Genome Property rules
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);

		// Init features with the subjects of each of our sufficient_for feature properties
		Feature feature = new Feature("xyz");
		FeatureProperty propRequired = FeatureProperty.create("63238");
		FeatureProperty propSufficient = FeatureProperty.create("63239");
		feature.addProperty(propSufficient);
		
		// Init a genome that this feature is annotated on
		Genome genome = new Genome();
		feature.setGenome(genome);
		
		// Add these facts to our knowledgebase
		engine.addFact(feature);
		engine.addFact(propRequired);
		engine.addFact(propSufficient);
		engine.addFact(genome);
		
		// Assigned genome properties before firing rules
		assertEquals(0, genome.getProperties().size());
		
		// Fire our engine
		engine.fireAllRules();
		
		// Test if the GenomeProperty was assigned
		assertEquals(1, genome.getProperties().size());
	}

	@After
	public void tearDown() throws Exception {
		engine = null;
	}
}
