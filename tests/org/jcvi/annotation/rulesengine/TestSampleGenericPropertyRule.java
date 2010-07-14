package org.jcvi.annotation.rulesengine;
import java.net.URL;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.builder.ResourceType;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.HmmHit;
import org.jcvi.annotation.facts.Property;
import org.junit.After;
import org.junit.Before;

public class TestSampleGenericPropertyRule extends TestCase {
	private RulesEngine engine;
	private Feature orfA;
	private Feature orfB;
	
	@Before
	public void setUp() throws Exception {
		
		engine = new RulesEngine();
		
		/*
		URL dslrUrl = engine.getClass().getResource("../rules/SampleGenericPropertyRule.dslr");
		URL dslUrl = engine.getClass().getResource("../rules/BrainGrabRulesTranslator.dsl");
		engine.addResource(dslUrl.toString(), ResourceType.DSL);
		engine.addResource(dslrUrl.toString(), ResourceType.DSLR);
		*/
		
		URL drlUrl = engine.getClass().getResource("../rules/SampleGenericPropertyRule.drl");
		engine.addResource(drlUrl.toString(), ResourceType.DRL);
		
		// Some reference feature
        Feature source = new Feature("source", "assembly");

        // Create two features nearby each other on our reference
		orfA = new Feature("orfA", "ORF", 1, 101, 1, source);
		orfB = new Feature("orfB", "ORF", 200, 300, 1, source);
		
		// These features have HMM hits to TIGR02607 and PF05015
		HmmHit hit1 = new HmmHit("orfA", "TIGR02607");
		HmmHit hit2 = new HmmHit("orfB", "PF05015");
		
		// orfA is assigned a twinarg property
		Property prop = FeatureProperty.create("TWINARG"); 
		prop.getAttributes().put("TWINARG", 1);
		orfA.addProperty(prop);	
		
        engine.addFact(orfA);
        engine.addFact(orfB);
		engine.addFact(hit1);
		engine.addFact(hit2);
		engine.addFact(prop);
		engine.fireAllRules();
	}
	
	public void testOrfAToxinProperty() {
		 List<Property> properties = orfA.getProperties();
		 boolean isAssigned = false;
		 for (Property prop : properties)  {
			 if (prop.getAttributes().containsKey("TOXIN_ANTITOXIN")) {
				 isAssigned = true;
			 }
		 }
		 assertEquals(true, isAssigned);
	}
	public void testOrfBToxinProperty() {
		 List<Property> properties = orfB.getProperties();
		 boolean isAssigned = false;
		 for (Property prop : properties)  {
			 if (prop.getAttributes().containsKey("TOXIN_ANTITOXIN")) {
				 isAssigned = true;
			 }
		 }
		 assertEquals(true, isAssigned);
	}
		
	
	public void testSomethingPropertyOnOrfA() {
		 List<Property> properties = orfA.getProperties();
		 boolean isAssigned = false;
		 for (Property prop : properties)  {
			 if (prop.getAttributes().containsKey("SOMETHING")) {
				 isAssigned = true;
			 }
		 }
		 assertEquals(true, isAssigned);	 
	}
	public void testSomethingPropertyOnOrfB() {
		 List<Property> properties = orfB.getProperties();
		 boolean isAssigned = false;
		 for (Property prop : properties)  {
			 if (prop.getAttributes().containsKey("SOMETHING")) {
				 isAssigned = true;
			 }
		 }
		 
		 // OrfB should not have the SOMETHING property
		 // because Orf B is not a TWINARG
		 assertEquals(false, isAssigned);	 
	}
	
	@After
	public void tearDown() throws Exception {
		engine = null;
	}

}
