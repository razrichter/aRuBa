package org.jcvi.annotation.rulesengine.test;


import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.junit.After;
import org.junit.Before;
import org.jcvi.annotation.facts.*;
import org.jcvi.annotation.rulesengine.*;

public class TestBlastPHitRule extends TestCase {

	private RulesEngine engine;
	private Feature orf;
	private Annotation ann;
	@Before
	public void setUp() throws Exception {
		engine = new RulesEngine();
		URL url = engine.getClass().getResource("/org/jcvi/annotation/rulesengine/SampleBlastHit.drl");
		engine.addResource(url.toString(), ResourceType.DRL);
		this.orf = new Feature("testorf", new SourceMolecule(new Genome("bac"), "1"), 0, 110, 1, FeatureType.ORF);
		
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
		this.ann = orf.getAssertedAnnotations().get(0);
	}
	
	public void testSampleBlastCommonName() {
		assert(this.ann.getCommonName().equals("exosporium protein K"));
	}
	public void testSampleBlastGeneSymbol() {
		assert(ann.getGeneSymbol().equals("exsK"));
	}
	public void testSampleBlastEcNumber() {
		assert(ann.getEcNumber() == null);
	}
	public void testSampleBlastGoIds() {
		List<String> goIds = ann.getGoIds();
		assert(goIds.containsAll(Arrays.asList("GO:0043592","GO:0003674","GO:0008150")));
	}
	public void testSampleBlastRoleIds() {
		List<String> roleIds = ann.getRoleIds();
		assert(roleIds.contains("705"));
	}
	public void testFail() {
		assert(false); // should fail
	}

	@After
	public void tearDown() throws Exception {
		engine = null;
	}

}
