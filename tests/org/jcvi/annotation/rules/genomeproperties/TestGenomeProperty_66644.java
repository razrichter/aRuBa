package org.jcvi.annotation.rules.genomeproperties;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.dao.RdfFactDAO;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.rulesengine.RulesEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestGenomeProperty_66644 extends TestCase {

	private RdfFactDAO dao;
	private RulesEngine engine = new RulesEngine();
	
    @Before
    public void setUp() throws Exception {
		URL rdfUrl = this.getClass().getResource("data/GenomeProperty_66644.rdf");
		URL n3Url = this.getClass().getResource("data/GenomeProperty_66644.n3");
		dao = new RdfFactDAO(n3Url, "N3");
		engine.addFacts(dao);
		
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
		
		// Add our Genome Property rules
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);

		// Init features with the subjects of each of our sufficient_for feature properties
		Feature feature1 = new Feature("feature_with_TIGR03936");
		Feature feature2 = new Feature("feature_with_TIGR03960");
		FeatureProperty featureprop1 = FeatureProperty.create("TIGR03936");
		FeatureProperty featureprop2 = FeatureProperty.create("TIGR03960");
		feature1.addProperty(featureprop1);
		feature2.addProperty(featureprop2);
		
		// Init a genome that this feature is annotated on
		Genome genome = new Genome();
		feature1.setGenome(genome);
		feature2.setGenome(genome);
		
		// Add these facts to our knowledgebase
		engine.addFact(feature1);
		engine.addFact(feature2);
		engine.addFact(featureprop1);
		engine.addFact(featureprop2);
		engine.addFact(genome);
		
		// Assigned genome properties before firing rules
		assertEquals(0, genome.getProperties().size());
		
		// Fire our engine
		engine.fireAllRules();
		
		// Get the properties for our feature
		List<Map<String, Object>> featureprops1 = feature1.getProperties();
		List<Map<String, Object>> featureprops2 = feature2.getProperties();

		// Test rule conditions are true
		assertTrue(featureprops1.contains(featureprop1));
		assertTrue(featureprops2.contains(featureprop2));
		
		// Test rule consequences were asserted
		// featureprop1 (TIGR03936) sufficient_for featureprop2 (63238)
		assertTrue(featureprops1.contains(FeatureProperty.create("63238")));
		assertTrue(featureprops2.contains(FeatureProperty.create("63239")));
	}

	@Test
	public void testRequiredByPredicate() {
		
		/*
		 * FeatureProperty_63238 required_by GenomeProperty_66644
		 * FeatureProperty_63239 required_by GenomeProperty_66644
		 */

		// Add our Genome Property rule
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);

		// Create a genome, and assign it the Genome Property of interest
		Genome genome = new Genome();
		GenomeProperty genomeprop = GenomeProperty.create("66644");
		genome.addProperty(genomeprop);
		
		Feature feature = new Feature("123");
		FeatureProperty featureprop1 = FeatureProperty.create("63238");
		FeatureProperty featureprop2 = FeatureProperty.create("63239");
		feature.setGenome(genome);
		
		// Add our facts to the knowledge session
		engine.addFact(genome);
		engine.addFact(genomeprop);
		engine.addFact(feature);
		engine.addFact(featureprop1);
		engine.addFact(featureprop2);
		
		// Before firing rules, we expect our 1 genome property
		assertEquals(1, genome.getProperties().size());
		
		// Fire our engine
		engine.fireAllRules();
		
		// After, we expect our genome property to be removed
		assertEquals(0, genome.getProperties().size());
		
	}

	@Test
	public void testRequiredByPredicateWithOneOfTwoRequiredProperties() {
		
		/*
		 * FeatureProperty_63238 required_by GenomeProperty_66644
		 * FeatureProperty_63239 required_by GenomeProperty_66644
		 */

		// Add our Genome Property rule
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);

		// Create a genome, and assign it the Genome Property of interest
		Genome genome = new Genome();
		GenomeProperty genomeprop = GenomeProperty.create("66644");
		genome.addProperty(genomeprop);
		
		Feature feature = new Feature("123");
		FeatureProperty featureprop1 = FeatureProperty.create("63238");
		FeatureProperty featureprop2 = FeatureProperty.create("63239");
		feature.setGenome(genome);
	
		// Test with 1 of 2 required properties
		feature.addProperty(featureprop1);
		
		// Add our facts to the knowledge session
		engine.addFact(genome);
		engine.addFact(genomeprop);
		engine.addFact(feature);
		engine.addFact(featureprop1);
		engine.addFact(featureprop2);
		
		// Before firing rules, we expect our 1 genome property
		assertEquals(1, genome.getProperties().size());
		
		// Fire our engine
		engine.fireAllRules();
		
		// After, we expect our genome property to be removed
		assertEquals(0, genome.getProperties().size());
	}

	@Test
	public void testRequiredByPredicateWithBothRequiredProperties() {
		
		/*
		 * FeatureProperty_63238 required_by GenomeProperty_66644
		 * FeatureProperty_63239 required_by GenomeProperty_66644
		 */

		// Add our Genome Property rule
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);

		// Create a genome, and assign it the Genome Property of interest
		Genome genome = new Genome();
		GenomeProperty genomeprop = GenomeProperty.create("66644");
		genome.addProperty(genomeprop);
		
		Feature feature = new Feature("123");
		FeatureProperty featureprop1 = FeatureProperty.create("63238");
		FeatureProperty featureprop2 = FeatureProperty.create("63239");
		feature.setGenome(genome);
	
		// Test with 1 of 2 required properties
		feature.addProperty(featureprop1);
		feature.addProperty(featureprop2);
		
		// Add our facts to the knowledge session
		engine.addFact(genome);
		engine.addFact(genomeprop);
		engine.addFact(feature);
		engine.addFact(featureprop1);
		engine.addFact(featureprop2);
		
		// Before firing rules, we expect our 1 genome property
		assertEquals(1, genome.getProperties().size());
		
		// Fire our engine
		engine.fireAllRules();
		
		// After, we expect to retain our genome property
		assertEquals(1, genome.getProperties().size());
	}
	
	@After
	public void tearDown() throws Exception {
		//engine = null;
	}
}
