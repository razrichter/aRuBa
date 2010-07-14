package org.jcvi.annotation.rules.genomeproperties;

import java.net.URL;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.dao.RdfFactDAO;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.rulesengine.RulesEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRequiredByRule extends TestCase {

	private RdfFactDAO dao;
	private RulesEngine engine = new RulesEngine();
	
    @Before
    public void setUp() throws Exception {
		URL n3Url = this.getClass().getResource("data/GenomePropertySufficientRequired.n3");
		dao = new RdfFactDAO(n3Url, "N3");
		engine.addFacts(dao);
		//engine.setConsoleLogger();
    }
	
	@Test
	public void testRdfConverter() {
		assertEquals(1, dao.getNumGenomeProperties());
		assertEquals(3, dao.getNumFeatureProperties());
		assertEquals(4, dao.getRelationships().size());
		assertEquals(8, dao.getTotalFacts());
	}

	@Test
	public void testRequiredByRule() {
		
		// Add our Required By rule
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);
		
		// Expectations:
		//  1) 	There is a PropertyRelationship in the knowledgebase that expresses
		//  	gp:FeatureProperty_63238 :required_by gp:GenomeProperty_66644, like
		//		PropertyRelationship(FeatureProperty.create("63238"), RelationshipType.REQUIRED_BY, GenomeProperty("66644"))
		//  2) 	The requiredby.drl will collect this relationship, and sum up the value
		//		of the required FeatureProperty
		// 	3)	The consequence will assign required=1, filled=1 and value=1 to the GenomeProperty
		
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
		assertEquals(1.0, gp.getRequired());
		assertEquals(1.0, gp.getFilled());
		assertEquals(1.0, gp.getValue());
	}

	@After
	public void tearDown() throws Exception {
		engine = null;
	}
}
