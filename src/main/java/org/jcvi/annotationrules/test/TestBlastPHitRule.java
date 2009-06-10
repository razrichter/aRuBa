package org.jcvi.annotationrules.test;


import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.junit.After;
import org.junit.Before;
import org.jcvi.annotationrules.*;
import org.jcvi.genemodel.*;

public class TestBlastPHitRule extends TestCase {

	private RulesEngine engine;
	
	@Before
	public void setUp() throws Exception {
		engine = new RulesEngine();
		URL url = engine.getClass().getResource("/org/jcvi/annotationrules/SampleBlastHit.drl");
		engine.addResource(url.toString(), ResourceType.DRL);
	}
	
	public void testSampleBlastHit() {
		
		Feature orf = new Feature("testorf", new SourceMolecule(new Genome("bac"), "1"), 0, 110, 1, FeatureType.ORF);
		
		BlastpHit hit1 = new BlastpHit(orf, "RF|NP_844922.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
		
		BlastpHit hit2 = new BlastpHit(orf, "RF|NOT_IT.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 110, 100, 205, 1, 95.0, 82.0);
		
		BlastpHit hit3 = new BlastpHit(orf, "RF|NOT_IT.1", 0.001, 170, 170, 0.002,
				100, 200, 1, 1000, 100, 205, 1, 95.0, 82.0);

		
		engine.addFact(hit1);
		engine.addFact(hit2);
		engine.addFact(hit3);
		engine.addFact(orf);
		engine.fireAllRules();
		
	}

	@After
	public void tearDown() throws Exception {
		engine = null;
	}

}
