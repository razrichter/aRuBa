package org.jcvi.annotation.rules.genomeproperties;

import java.net.URL;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.drools.runtime.rule.FactHandle;
import org.jcvi.annotation.dao.RdfFactDAO;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.Property;
import org.jcvi.annotation.rulesengine.RulesEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSufficesAndRequiredRules extends TestCase {

	private RdfFactDAO dao;
	private RulesEngine engine;
	
    @Before
    public void setUp() throws Exception {
    	engine = new RulesEngine();
		String file = "data/GenomePropertySufficientRequired.n3"; // "../../dao/data/genomeproperties.n3";
    	URL n3Url = this.getClass().getResource(file);
		dao = new RdfFactDAO(n3Url, "N3");
		engine.addFacts(dao);
		
    }
	
	@Test
	public void DONOTtestRdfConverter() {
		assertTrue(dao instanceof org.jcvi.annotation.dao.RdfFactDAO);
		assertEquals(1, dao.getNumGenomeProperties());
		assertEquals(2, dao.getNumFeatureProperties());
		assertEquals(2, dao.getRelationships().size());
		assertEquals(5, dao.getTotalFacts());
	}

	@Test
	public void testRequiredByRule() {
		System.out.println("\ntestRequiredByRule()...");
		
		// Add our Required By rule
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);
		
		// Expectations:
		//  1) 	There is a PropertyRelationship in the knowledgebase that expresses
		//  	gp:FeatureProperty_63238 :required_by gp:GenomeProperty_66644, like
		//		PropertyRelationship(FeatureProperty.create("63238"), RelationshipType.REQUIRED_BY, GenomeProperty("66644"))
		//  2) 	The requiredby.drl will collect this relationship, and sum up the value
		//		of the required FeatureProperty
		// 	3)	The consequence will assign required=1, filled=1 and value=1 to the GenomeProperty
		
		// Init genome containing our required feature property
		Genome genome = new Genome();
		FeatureProperty propRequired = FeatureProperty.create("63238");
		propRequired.setValue(1);
		genome.addProperty(propRequired);
		
		// Add these facts to our knowledgebase
		engine.addFact(genome);
		engine.addFact(propRequired);	
		
		// Fire our engine
		engine.fireAndDispose();
		
		// This gets this property which we expect to be in the GenomeProperty.propsCache
		GenomeProperty gp = GenomeProperty.create("66644");
		
		assertEquals(1.0, propRequired.getValue());
		assertEquals(1.0, gp.get("required"));
		assertEquals(1.0, gp.get("filled"));
		assertEquals(1.0, gp.getValue());
	}

	@Test
	public void testAllRules() {
		System.out.println("\ntestAllRules()...");
		
		// Add all of our genome properties rules
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);
		engine.addResource(this.getClass().getResource("AboveTrustedCutoff.drl"), ResourceType.DRL);

		// Init feature with our sufficient property
		Feature feature = new Feature("xyz");
		FeatureProperty propSufficient = FeatureProperty.create("123");
		feature.addProperty(propSufficient);

		// Add these facts to our knowledgebase
		engine.addFact(feature);
		engine.addFact(propSufficient);	
		
		// Fire our engine
		engine.fireAndDispose();
		
		// We expect that this FeatureProperty_123, should assign FeatureProperty_63239
		// gp:FeatureProperty_123 sufficient_for FeatureProperty_63239
		FeatureProperty propAssigned = FeatureProperty.create("63239");
		assertTrue(feature.getProperties().contains(propAssigned));
		assertEquals(1.0, propAssigned.getValue());
		
		// Get our GenomeProperty from the propsCache, and 
		// test that it has been modified by Drools
		GenomeProperty genomepropAssigned = GenomeProperty.create("66644");
		assertTrue(feature.getProperties().contains(genomepropAssigned));
		assertEquals(1.0, genomepropAssigned.getValue());
		
	}
	
	@Test
	public void testFeaturePropertySufficientForFeatureProperty() {
		System.out.println("\ntestFeaturePropertySufficientForFeatureProperty()...");
		
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
		FeatureProperty propSufficient = FeatureProperty.create("123");
		feature.addProperty(propSufficient);
		
		// Add these facts to our knowledgebase
		engine.addFact(feature);
		engine.addFact(propSufficient);	
		
		// Fire our engine
		engine.fireAndDispose();
		
		// We expect that this FeatureProperty_123, should assign FeatureProperty_63238
		FeatureProperty propAssigned1 = FeatureProperty.create("63238");
		FeatureProperty propAssigned2 = FeatureProperty.create("63239");
		assertTrue(feature.getProperties().contains(propAssigned1));
		assertTrue(feature.getProperties().contains(propAssigned2));
		assertEquals(1.0, propAssigned1.getValue());
		assertEquals(1.0, propAssigned2.getValue());
		
		// and, since FeatureProperty_63238 is required by GenomeProperty_66644,
		// this should trigger a recalculation of the GenomeProperty value
		GenomeProperty gp = GenomeProperty.create("66644");
		assertEquals(1.0, gp.get("required"));
		assertEquals(1.0, gp.get("filled"));
		assertEquals(1.0, gp.getValue());
		assertTrue(feature.getProperties().contains(gp));
	}

	@Test
	public void testSufficesRuleOnFeature() {
		System.out.println("\ntestSufficesRuleOnFeature");
		System.out.println("Testing rule: Property (on Feature) sufficient_for GenomeProperty");
		// Tests rule: 	"Property (on Feature) sufficient_for GenomeProperty"
		
		// Add our suffices rule
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);
		
		// Expectations:
		//  1) 	There is a Property Relationship added from the RdfFactDAO that expresses that
		// 		gp:FeatureProperty_63239 :sufficient_for	gp:GenomeProperty_66644;
		//  2) 	"Property (on Feature) sufficient_for GenomeProperty" (suffices.drl)
		//		will assign the consequence property to the feature
		//  3) 	"Property (on Genome) sufficient_for GenomeProperty"

		// Init feature with our sufficient property
		Feature feature = new Feature("xyz");
		FeatureProperty propSufficient = FeatureProperty.create("63239");
		feature.addProperty(propSufficient);

		// Add these facts to our knowledgebase
		engine.addFact(feature);
		engine.addFact(propSufficient);	
		
		// Fire our engine
		engine.fireAndDispose();
		
		// Get our GenomeProperty from the propsCache, and 
		// test that it has been modified by Drools
		GenomeProperty propAssigned = GenomeProperty.create("66644");
		assertTrue(feature.getProperties().contains(propAssigned));
		assertEquals(1.0, propAssigned.getValue());
	}

	@Test
	public void testSufficesRuleOnGenome() {
		System.out.println("\ntestSufficesRuleOnGenome");
		System.out.println("Testing rule: Property (on Genome) sufficient_for GenomeProperty");
		// Tests rule: 	"Property (on Genome) sufficient_for FeatureProperty"
		
		// Add our suffices rule
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);
		
		// Expectations:
		//  1) 	There is a Property Relationship added from the RdfFactDAO that expresses that
		// 		gp:FeatureProperty_63239 :sufficient_for	gp:GenomeProperty_66644;
		//  2) 	"Property (on Feature) sufficient_for GenomeProperty" (suffices.drl)
		//		will assign the consequence property to the feature
		//  3) 	"Property (on Genome) sufficient_for GenomeProperty"

		// Init genome with our sufficient property
		Genome genome = new Genome();
		FeatureProperty propSufficient = FeatureProperty.create("63239");
		genome.addProperty(propSufficient);

		// Add these facts to our knowledgebase
		engine.addFact(genome);
		engine.addFact(propSufficient);	
		
		// Fire our engine
		engine.fireAndDispose();
		
		// Get our GenomeProperty from the propsCache, and 
		// test that it has been modified by Drools
		GenomeProperty propAssigned = GenomeProperty.create("66644");
		assertTrue(genome.getProperties().contains(propAssigned));
		assertEquals(1.0, propAssigned.getValue());
	}

	@Test
	public void testSufficesAndRequiredRules() {
		System.out.println("\ntestSufficesAndRequiredRules");
		
		// Add our suffices rule
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);
		
		// Expectations:
		//  1) 	There is a Property Relationship added from the RdfFactDAO that expresses that
		// 		gp:FeatureProperty_63239 :sufficient_for	gp:GenomeProperty_66644;
		//  2) 	"Property (on Feature) sufficient_for GenomeProperty" (suffices.drl)
		//		will assign the consequence property to the feature
		//  3) 	"Property (on Genome) sufficient_for GenomeProperty"

		// Init feature with our sufficient property
		Feature feature = new Feature("xyz");
		FeatureProperty propSufficient = FeatureProperty.create("63239");
		feature.addProperty(propSufficient);

		// Init genome containing our required feature property
		Genome genome = new Genome();
		FeatureProperty propRequired = FeatureProperty.create("63238");
		propRequired.setValue(1);
		genome.addProperty(propRequired);
		feature.setGenome(genome);

		// Add these facts to our knowledgebase
		engine.addFact(feature);
		engine.addFact(propSufficient);	

		// Let's test chaining the sufficient step with our required by rule, to 
		// get an updated calculation for the Genome Property
		

		// Add these facts to our knowledgebase
		engine.addFact(genome);
		engine.addFact(feature);
		engine.addFact(propRequired);	
		engine.addFact(propSufficient);	
		
		// Fire our engine
		engine.fireAndDispose();
		
		// This gets this property which we expect to be in the GenomeProperty.propsCache
		GenomeProperty gp = GenomeProperty.create("66644");
		assertEquals(1.0, propRequired.getValue());
		assertEquals(1.0, gp.get("required"));
		assertEquals(1.0, gp.get("filled"));
		assertEquals(1.0, gp.getValue());
		assertTrue(feature.getProperties().contains(gp));
		assertTrue(genome.getProperties().contains(gp));
	}
	
	@Test
	public void testHasSufficientButNotRequired() {
		System.out.println("\ntestHasSufficientButNotRequired");
		
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
		assertEquals(1, feature.getProperties().size());
		
		// Fire our engine
		engine.fireAllRules();
		
		// Test if the GenomeProperty was assigned
		GenomeProperty gp = GenomeProperty.create("66644");
		assertTrue(feature.getProperties().contains(gp));
		assertEquals(1.0, gp.getValue());
		assertEquals(2, feature.getProperties().size());
	}

	public void endSession() {
		engine.getKsession().dispose();
		engine.setKbase(null);
	}
	
	@After
	public void tearDown() throws Exception {
		endSession();
		engine = null;
	}
}
