package org.jcvi.sample;

import java.net.URL;
import java.util.HashSet;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.rulesengine.RulesEngine;
import org.jcvi.sample.facts.Fact;
import org.junit.After;
import org.junit.Before;

public class TestSimpleInference extends TestCase {
	private RulesEngine engine;
	private HashSet<Fact> facts = new HashSet<Fact>();
	
	@Before
	public void setUp() throws Exception {
		
		facts.add(Fact.create("A"));
		facts.add(Fact.create("B"));
		facts.add(Fact.create("D"));
		
		engine = new RulesEngine();
		URL url = this.getClass().getResource("SimpleInference.drl");
		if (url == null) {
			throw new RuntimeException("Can't find drl file");
		}
		engine.addResource(url.toString(), ResourceType.DRL);
		engine.addGlobal("assertedFacts", facts);
		
		for (Fact f : facts) {
			engine.addFact(f);
		}
		engine.fireAllRules();
	}
	
	public void testBDoesNotExist() {
		assertFalse(facts.contains(Fact.create("B")));
	}
	
	public void testCDoesNotExist() {
		assertFalse(facts.contains(Fact.create("C")));
	}

	@After
	public void tearDown() throws Exception {
		engine = null;
	}

}
