package org.jcvi.annotation.rulesengine;

import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.facts.BlastHit;
import org.jcvi.annotation.facts.Feature;
import org.junit.After;
import org.junit.Before;

public class TestBlastPHitNearBlastPHitRule extends TestCase {

	private RulesEngine engine;
	private ArrayList<Feature> orfs = new ArrayList<Feature>();
	
	@Before
	public void setUp() throws Exception {
		
		engine = new RulesEngine();
		
		URL dslUrl = engine.getClass().getResource("../rules/BrainGrabRulesTranslator.dsl");
		URL dslrUrl = engine.getClass().getResource("../rules/BlastPHitNearBlastPHit.dslr");
		engine.addResource(dslUrl.toString(), ResourceType.DSL);
		engine.addResource(dslrUrl.toString(), ResourceType.DSLR);
		// URL drlUrl = engine.getClass().getResource("../rules/testNearbyRule.drl");
		// engine.addResource(drlUrl.toString(), ResourceType.DRL);
		/*
		URL url = engine.getClass().getResource("../rules/SampleBlastHit.drl");
		engine.addResource(url.toString(), ResourceType.DRL);
		*/
		orfs.add(new Feature("orfA", "ORF", 1, 101, 1));
		orfs.add(new Feature("orfB", "ORF", 200, 300, 0));
		orfs.add(new Feature("orfC", "ORF", 4000, 4100, 0));
        Feature source = new Feature("source", "assembly");
		for (Feature f : orfs) {
		    f.setSource(source);
		}
		
		// Only orfA (via orfB) should evaluate true according to our SampleBlastHit rule
		BlastHit hit1 = new BlastHit("blastp","orfA", "SP|P35158", 0.001, 175, 175, 0.002,
				1, 101, 1, 105, 100, 205, 1, 95.0, 92.0);
		hit1.setQueryLength(orfs.get(0).getLength());
		BlastHit hit2 = new BlastHit("blastp","orfC", "SP|P35158", 0.001, 175, 175, 0.002,
                1, 101, 1, 105, 100, 205, 1, 95.0, 92.0);
        hit2.setQueryLength(orfs.get(1).getLength());
        BlastHit hit3 = new BlastHit("blastp","orfB", "SP|P35150", 0.001, 290, 290, 0.002,
				100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
        hit3.setQueryLength(orfs.get(2).getLength());
		
        engine.addFacts(orfs);
		engine.addFact(hit1);
		engine.addFact(hit2);
		engine.addFact(hit3);
		engine.fireAllRules();
	}
	
	public void testOrfaIsAnnotated() {
	    assertEquals("OrfA is annotated", 1, orfs.get(0).getAssertedAnnotations().size());
	}
	
	public void testOrfbIsNotAnnotated() {
        assertEquals("OrfB is not annotated", 0, orfs.get(1).getAssertedAnnotations().size());
	}
	public void testOrfcIsNotAnnotated() {
	    assertEquals("OrfC is not annotated", 0, orfs.get(2).getAssertedAnnotations().size());
	}
	
	
	@After
	public void tearDown() throws Exception {
		engine = null;
	}

}
