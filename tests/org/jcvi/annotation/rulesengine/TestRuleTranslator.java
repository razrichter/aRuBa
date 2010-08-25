package org.jcvi.annotation.rulesengine;

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
import org.jcvi.annotation.facts.Genome;
import org.jcvi.annotation.facts.GenomeProperty;
import org.jcvi.annotation.facts.Taxon;
import org.junit.Before;

public class TestRuleTranslator extends TestCase {

	private Feature feat;
	private Annotation annot = new Annotation();
	private Taxon taxon;

	@Before
	public void setUp() throws Exception {

		// ADD BRAINGRAB RULES TO KBUILDER AND CREATE KNOWLEDGEBASE
		final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(
				"/org/jcvi/annotation/rules/TestTranslatorSolution.drl", this
						.getClass()), ResourceType.DRL);
		if (kbuilder.hasErrors()) {
			System.err.println(kbuilder.getErrors().toString());
			throw new RuntimeException("Unable to compile rules.");
		}
		final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		final StatefulKnowledgeSession ksession = kbase
				.newStatefulKnowledgeSession();

		// ADD FACTS TO STATEFUL KNOWLEDGE SESSION
		feat = new Feature("pos");
		Feature feat1 = new Feature("n1");
		Feature feat2 = new Feature("n2");
		Feature feat3 = new Feature("n3");
		Feature feat4 = new Feature("n4");

		// Assign a taxon to our Feature
		taxon = new Taxon(1, "Geosporobacter");
		Taxon parent = new Taxon(0, "Firmicutes");
		taxon.setParent(parent);
		Genome genome = new Genome(taxon);

		// feat and feat1 are Firmicutes
		feat.setGenome(genome);
		feat1.setGenome(genome);

		// feat and feat2 have the right Go Id
		annot.addGoId("GO:0030435");
		feat.setAssignedAnnotation(annot);
		feat2.setAssignedAnnotation(annot);

		// feat3 has the other good GO Id
		Annotation annot2 = new Annotation();
		annot2.addGoId("GO:0043936");
		feat3.setAssignedAnnotation(annot);

		// feat and feat4 have the right genome property
		Double value = 0.6;
		GenomeProperty property = GenomeProperty.create("GenProp0610");
		property.setValue(value);
		feat.addProperty(property);
		feat4.addProperty(property);

		// Add the feature to our knowledgebase
		ksession.insert(feat);
		ksession.insert(feat1);
		ksession.insert(feat2);
		ksession.insert(feat3);
		ksession.insert(feat4);
		ksession.insert(annot);
		ksession.insert(annot2);
		ksession.insert(property);
		ksession.insert(taxon);
		ksession.insert(parent);

		// FIRE RULES, CLOSE AND SHUTDOWN
		ksession.fireAllRules();
		ksession.dispose();
	}

	public void testGoAnnotation() {
		// (1) Verify the rule conditions are true
		// 1a. Verify the GO annotation is what we expect
		assertTrue(annot.getGoIds().contains("GO:0008150"));

		// 1b. Verify the taxon restriction condition
		// assertTrue(taxon.getParentNames().contains("Firmicutes"));

		// (2) Verify the rule actions occurred
		// 2a. Verify the common name on the annotation was changed
		assertEquals("endospore", feat.getAssignedAnnotation().getCommonName());

	}
}
