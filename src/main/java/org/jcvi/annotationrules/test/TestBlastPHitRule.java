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
		
		// String featureId, SourceMolecule source, int start, int end,
		// int strand, int type
		
		Feature orf = new Feature("testorf", new SourceMolecule(new Genome("bac"), "1"), 0, 110, 1, FeatureType.ORF);
		
		/*
		 public BlastpHit(Feature query, String hitId, double evalue, double score,
			double bitScore, double pvalue, int queryStart, int queryEnd,
			int queryStrand, int hitLength, int hitStart, int hitEnd,
			int hitStrand, double percentSimilarity, double percentIdentity) {
			
		blastpHit : BlastpHit( hitId == "RF|NP_844922.1",
			bitScore >= 160, hitPercentLength >= 90,
			queryPercentLength >= 90,  percentIdentity >= 80,
			hitQueryLengthRatio <= 5 );
			
		 */
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
