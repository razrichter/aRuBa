package org.jcvi.annotation.rules.genomeproperties;

import java.net.URL;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.Aruba;
import org.jcvi.annotation.dao.RdfFactDAO;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.PropertyState;
import org.jcvi.annotation.rulesengine.RulesEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestGenomeProperty_1 extends TestCase {

	private RdfFactDAO dao;
	private Aruba aruba;
	private RulesEngine engine;
	private String genome = "gb6";
	
    @Before
    public void setUp() throws Exception {
    	aruba = new Aruba();
    	engine = aruba.getEngine();
		URL n3Url = this.getClass().getResource("data/GenomeProperty_1.n3");
		dao = new RdfFactDAO(n3Url, "N3");
		engine.addFacts(dao);
    }
	
	@Test
	public void testRdfConverter() {
		assertEquals(1, dao.getNumGenomeProperties());
		assertEquals(21, dao.getNumFeatureProperties());
		assertEquals(21, dao.getRelationships().size());
		assertEquals(43, dao.getTotalFacts());
		
	}

	@Test
	public void testGenomeProperty_1() {
		System.out.println("\ntest GenomeProperty 1...");
		
		// Add our Genome Property rules
		engine.addResource(this.getClass().getResource("AboveTrustedCutoff.drl"), ResourceType.DRL);
		engine.addResource(this.getClass().getResource("suffices.drl"), ResourceType.DRL);
		engine.addResource(this.getClass().getResource("requiredby.drl"), ResourceType.DRL);

		// Add a small genome database
		aruba.addSmallGenome(genome);
		
		// Get our GenomeProperty from the GenomeProperty.propsCache,
		// which was loaded during setup()
		GenomeProperty p = GenomeProperty.create("1");
		
		assertEquals(0.0, p.getFilled());
		assertEquals(0.0, p.getValue());
		assertEquals(0.0, p.getRequired());
		
		// Fire our engine
		engine.fireAndDispose();
		
		System.out.println(p.toStringReport());
		
		assertEquals(7.0, p.getFilled());
		assertEquals(7.0, p.getRequired());
		assertEquals(1.0, p.getValue());
		assertEquals(PropertyState.YES, p.getState());
	}

	@After
	public void tearDown() throws Exception {
		engine = null;
	}
}
