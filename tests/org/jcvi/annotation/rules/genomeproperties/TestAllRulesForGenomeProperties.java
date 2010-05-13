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

public class TestAllRulesForGenomeProperties extends TestCase {

	private RdfFactDAO dao;
	private RulesEngine engine = new RulesEngine();
	
    @Before
    public void setUp() throws Exception {
		String file = "../../dao/data/genomeproperties.n3";
    	URL n3Url = this.getClass().getResource(file);
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
	public void testAllRules() {
		
		// Add all of our genome properties rules
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);
		engine.addResource(this.getClass().getResource("AboveTrustedCutoff.drl"), ResourceType.DRL);
		//engine.addResource(this.getClass().getResource("PropertyState.drl"), ResourceType.DRL);
		
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

		// Let's test chaining the sufficient step with our required by rule, to 
		// get an updated calculation for the Genome Property
		
		// Init genome containing our required feature property
		// Genome genome = new Genome();
		// feature.setGenome(genome);
		// engine.addFact(genome);

		// Add these facts to our knowledgebase
		engine.addFact(feature);
		engine.addFact(propSufficient);	
		
		// Fire our engine
		engine.fireAndDispose();

		// We expect the propRequired to be assigned a value of 1
		FeatureProperty propAssigned = FeatureProperty.create("63239");
		assertTrue(feature.getProperties().contains(propAssigned));
		assertEquals(1.0, propAssigned.getValue());

		// This gets this property which we expect to be in the GenomeProperty.propsCache
		GenomeProperty gp = GenomeProperty.create("66644");
		assertEquals(1.0, gp.get("required"));
		assertEquals(1.0, gp.get("filled"));
		assertEquals(1.0, gp.getValue());
		assertTrue(feature.getProperties().contains(gp));
		// assertTrue(genome.getProperties().contains(gp));

	}

	@After
	public void tearDown() throws Exception {
		engine = null;
	}
}
