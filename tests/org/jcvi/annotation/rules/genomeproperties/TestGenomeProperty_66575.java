package org.jcvi.annotation.rules.genomeproperties;

import java.net.URL;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.dao.RdfFactDAO;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.FeaturePropertyFactory;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.Property;
import org.jcvi.annotation.rulesengine.RulesEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestGenomeProperty_66575 extends TestCase {

	private RdfFactDAO dao;
	private RulesEngine engine;
	
    @Before
    public void setUp() throws Exception {
    	engine = new RulesEngine();
		URL n3Url = this.getClass().getResource("data/GenomeProperty_66575.n3");
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
	public void testFeaturePropertySufficientForFeatureProperty() {
		System.out.println("\ntest suffices...");
		
		// Add our suffices rule
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);
		
		// Expectations:
		//  1) 	There is a Property Relationship added from the RdfFactDAO that expresses that
		// 		gp:FeatureProperty_63239 :sufficient_for	gp:GenomeProperty_66644;
		//  2) 	"Property (on Feature) sufficient_for GenomeProperty" (suffices.drl)
		//		will assign the consequence property to the feature
		//  3) 	"Property (on Genome) sufficient_for GenomeProperty"

		// Init feature with our sufficient property
		Feature feature = new Feature("xyz");
		FeatureProperty propSufficient = FeatureProperty.create("TIGR03720"); // ("TIGR03720");
		FeatureProperty propAssigned = FeatureProperty.create("62994");
		feature.addProperty(propSufficient);
		
		// We expect the rule engine to assign this property
		assertFalse(feature.getProperties().contains(propAssigned));
		
		// Add these facts to our knowledgebase
		engine.addFact(feature);
		engine.addFact(propSufficient);	

		// Fire our engine
		engine.fireAndDispose();
		
		assertTrue(feature.getProperties().contains(propAssigned));
		assertEquals(1.0, propAssigned.getValue());
		
		// and, since FeatureProperty_63238 is required by GenomeProperty_66644,
		// this should trigger a recalculation of the GenomeProperty value
		GenomeProperty gp = GenomeProperty.create("66575");
		assertEquals(1.0, gp.get("required"));
		assertEquals(1.0, gp.get("filled"));
		assertEquals(1.0, gp.getValue());
	}

	@Test
	public void testGenomeProperty66575() {
		System.out.println("\ntest GenomeProperty 66575...");
		
		// Add our Genome Property rules
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);

		// Init features with the subjects of each of our sufficient_for feature properties
		Feature feature = new Feature("feature_with_TIGR03720");
		FeatureProperty featureprop = FeatureProperty.create("TIGR03720");
		feature.addProperty(featureprop);
		
		// Init a genome that this feature is annotated on
		Genome genome = new Genome();
		feature.setGenome(genome);
		
		// Add these facts to our knowledgebase
		engine.addFact(feature);
		engine.addFact(featureprop);
		engine.addFact(genome);
		
		// Assigned genome properties before firing rules
		assertEquals(0, genome.getProperties().size());
		
		// Fire our engine
		engine.fireAndDispose();
		
		// This gets this property which we expect to be in the GenomeProperty.propsCache
		GenomeProperty gp = GenomeProperty.create("66575");
		
		FeatureProperty featurepropAsserted = FeatureProperty.create("62994");
		
		
		// assertEquals(1.0, featurepropAsserted.getValue());
		assertEquals(1.0, gp.get("required"));
		assertEquals(1.0, gp.get("filled"));
		assertEquals(1.0, gp.getValue());
	}

	@After
	public void tearDown() throws Exception {
		engine = null;
	}
}
