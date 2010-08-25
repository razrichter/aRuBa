package org.jcvi.annotation.rulesengine;

import java.util.List;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.FeatureProperty;
import org.jcvi.annotation.facts.HmmHit;
import org.jcvi.annotation.facts.Property;
import org.junit.Before;

public class TestSampleGenericPropertyRule extends TestCase {
	private Feature orfA;
	private Feature orfB;

	@Before
	public void setUp() throws Exception {

		// ADD BRAINGRAB RULES TO KBUILDER AND CREATE KNOWLEDGEBASE
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(
				"/org/jcvi/annotation/rules/SampleGenericPropertyRule.drl",
				this.getClass()), ResourceType.DRL);
		if (kbuilder.hasErrors()) {
			System.err.println(kbuilder.getErrors().toString());
			throw new RuntimeException("Unable to compile rules.");
		}
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		final StatefulKnowledgeSession ksession = kbase
				.newStatefulKnowledgeSession();

		// ADD FACTS TO STATEFUL KNOWLEDGE SESSION
		Feature source = new Feature("source", "assembly");

		// Create two features nearby each other on our reference
		orfA = new Feature("orfA", "ORF", 1, 101, 1, source);
		orfB = new Feature("orfB", "ORF", 200, 300, 1, source);

		// These features have HMM hits to TIGR02607 and PF05015
		HmmHit hit1 = new HmmHit("orfA", "TIGR02607");
		HmmHit hit2 = new HmmHit("orfB", "PF05015");

		// orfA is assigned a twinarg property
		Property prop = FeatureProperty.create("TWINARG");
		orfA.addProperty(prop);

		FeatureProperty someProp = FeatureProperty.create("SOMETHING");
		ksession.insert(someProp);

		ksession.insert(orfA);
		ksession.insert(orfB);
		ksession.insert(hit1);
		ksession.insert(hit2);
		ksession.insert(prop);

		FeatureProperty toxin = FeatureProperty.create("TOXIN_ANTITOXIN");
		orfA.addProperty(toxin);

		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();
	}

	public void testOrfAToxinProperty() {
		List<Property> properties = orfA.getProperties();
		boolean isAssigned = false;
		for (Property prop : properties) {
			if (prop.getId().equals("TOXIN_ANTITOXIN")) {
				isAssigned = true;
			}
		}
		assertEquals(true, isAssigned);
	}

	public void testOrfBToxinProperty() {
		List<Property> properties = orfB.getProperties();
		boolean isAssigned = false;
		for (Property prop : properties) {
			if (prop.getId().equals("TOXIN_ANTITOXIN")) {
				isAssigned = true;
			}
		}
		assertEquals(true, isAssigned);
	}

	public void testSomethingPropertyOnOrfA() {
		List<Property> properties = orfA.getProperties();
		boolean isAssigned = false;
		for (Property prop : properties) {
			if (prop.getId().equals("SOMETHING")) {
				isAssigned = true;
			}
		}
		assertEquals(true, isAssigned);
	}

	public void testSomethingPropertyOnOrfB() {
		List<Property> properties = orfB.getProperties();
		boolean isAssigned = false;
		for (Property prop : properties) {
			if (prop.getId().equals("SOMETHING")) {
				isAssigned = true;
			}
		}

		// OrfB should not have the SOMETHING property
		// because Orf B is not a TWINARG
		assertEquals(false, isAssigned);
	}
}
