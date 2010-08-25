package org.jcvi.annotation.rulesengine;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jcvi.annotation.facts.Annotation;
import org.jcvi.annotation.facts.Feature;
import org.jcvi.annotation.facts.HmmHit;
import org.junit.Before;

public class TestHmmHitRule extends TestCase {
	private ArrayList<Feature> orfs = new ArrayList<Feature>();

	@Before
	public void setUp() throws Exception {
		// ADD BRAINGRAB RULES TO KBUILDER AND CREATE KNOWLEDGEBASE
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(
				"/org/jcvi/annotation/rules/BrainGrabRulesTranslator.dsl", this
						.getClass()), ResourceType.DSL);
		kbuilder.add(ResourceFactory.newClassPathResource(
				"/org/jcvi/annotation/rules/BrainGrabRules.dslr", this
						.getClass()), ResourceType.DSLR);
		kbuilder.add(ResourceFactory.newClassPathResource(
				"/org/jcvi/annotation/rules/HmmHitRule.dslr", this.getClass()),
				ResourceType.DSLR);
		if (kbuilder.hasErrors()) {
			System.err.println(kbuilder.getErrors().toString());
			throw new RuntimeException("Unable to compile rules.");
		}
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		final StatefulKnowledgeSession ksession = kbase
				.newStatefulKnowledgeSession();

		// ADD FACTS TO STATEFUL KNOWLEDGE SESSION
		orfs.add(new Feature("testorf1", "ORF", 0, 110, 1));
		orfs.add(new Feature("testorf2", "ORF", 0, 110, 1));
		// Only hit1 should evaluate true according to our SampleBlastHit rule
		HmmHit hit1 = new HmmHit("testorf1", "TIGR02469");
		hit1.setStrongHit();
		HmmHit hit2 = new HmmHit("testorf1", "TIGR02467");
		hit2.setStrongHit();
		HmmHit hit3 = new HmmHit("testorf2", "TIGR02469");
		hit3.setWeakHit();
		HmmHit hit4 = new HmmHit("testorf2", "TIGR02467");
		hit4.setStrongHit();

		for (Feature f : orfs) {
			ksession.insert(f);
		}
		ksession.insert(hit1);
		ksession.insert(hit2);
		ksession.insert(hit3);
		ksession.insert(hit4);

		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();

	}

	public void testHitsAssigned() {
		Feature orf = orfs.get(0);
		assertTrue(orf.getFeatureId().equals("testorf1"));
		assertTrue(orf.getAssertedAnnotations().size() == 1);
		Annotation ann = orf.getAssertedAnnotations().get(0);
		assertTrue(ann.getCommonName().equals(
				"precorrin-6Y C5,15-methyltransferase (decarboxylating)"));
		assertTrue(ann.getEcNumbers().get(0).equals("2.1.1.132"));
		assertTrue(ann.getGeneSymbol().equals("cobL"));
		// TODO: I don't know how to properly check this. contains looks at
		// object id, List.equals() does as well
		// assertEquals(Arrays.asList("GO:0046140", "GO:0009236", "GO:0046025"),
		// ann.getGoIds());
		assertTrue(ann.getRoleIds().contains("79"));
	}

	public void testMissNotAssigned() {
		assertTrue(orfs.get(1).getFeatureId().equals("testorf2"));
		assertTrue(orfs.get(1).getAssertedAnnotations().size() == 0);
	}
}
